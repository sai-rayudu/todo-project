package com.sai.todo.service;

import java.time.LocalDateTime;
import java.security.SecureRandom;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.sai.todo.dto.ForgotPasswordRequest;
import com.sai.todo.dto.LoginRequest;
import com.sai.todo.dto.RegisterRequest;
import com.sai.todo.dto.RegisterResponse;
import com.sai.todo.dto.ResetPasswordRequest;
import com.sai.todo.dto.VerifyOtpRequest;
import com.sai.todo.entity.PasswordResetOtp;
import com.sai.todo.entity.User;
import com.sai.todo.exception.BadRequestException;
import com.sai.todo.exception.UserNotFoundException;
import com.sai.todo.repository.PasswordResetOtpRepository;
import com.sai.todo.repository.UserRepository;
import com.sai.todo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sai.todo.entity.UserRegistration;
import com.sai.todo.repository.UserRegistrationRepository;
import com.sai.todo.dto.VerifyRegistrationRequest;

@Service 
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final UserRegistrationRepository userRegistrationRepository;

    private static final int OTP_COOLDOWN_SECONDS=120;
    private static final int OTP_EXPIRATION_MINUTES=5;
    private static final int Max_OTP_ATTEMPTS=5;

    private static final int OTP_MIN=100000;
    private static final int OTP_RANGE=900000;


    public AuthService(AuthenticationManager authenticationManager,JwtService jwtService,
        UserRepository userRepository,PasswordEncoder passwordEncoder,MailService mailService,
        PasswordResetOtpRepository passwordResetOtpRepository,
    UserRegistrationRepository userRegistrationRepository){

        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.mailService=mailService;
        this.passwordResetOtpRepository=passwordResetOtpRepository;
        this.userRegistrationRepository=userRegistrationRepository;

    }

    public String login(LoginRequest request){

        Authentication authentication =authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user=(User) authentication.getPrincipal();
        String role=user.getRole();

        return jwtService.generateToken(authentication.getName(),role,user.getTokenVersion());
    }


    public RegisterResponse register(RegisterRequest request){
            if(userRepository.findByUsername(request.getUsername()).isPresent()){
                throw new BadRequestException("Username already exists");
            }
            if(userRepository.findByEmail(request.getEmail()).isPresent()){
                throw new BadRequestException("Email already exists");
            }

            Optional<UserRegistration> existingRegistration=userRegistrationRepository.findByEmail(request.getEmail());

            LocalDateTime now = LocalDateTime.now();

            if(existingRegistration.isPresent()){
                UserRegistration registration=existingRegistration.get();

                if(registration.getOtpExpiresAt().isAfter(now)){
                    throw new BadRequestException("Email verification is already pending");
                }
                registration.setUsername(request.getUsername());
                registration.setEmail(request.getEmail());
                registration.setPassword(passwordEncoder.encode(request.getPassword()));
                int otp=OTP_MIN+ new SecureRandom().nextInt(OTP_RANGE);

                registration.setOtpHash(passwordEncoder.encode(String.valueOf(otp)));

                registration.setOtpExpiresAt(now.plusMinutes(OTP_EXPIRATION_MINUTES));

                registration.setOtpAttempts(0);
                //registration.setCreatedAt(now);
                registration.setUpdatedAt(now);

                userRegistrationRepository.save(registration);

                mailService.sendEmail(
                    registration.getEmail(),
                    "Email Verification OTP",
                    "Your OTP is:"+otp
                );
                return new RegisterResponse(
                    "Registration OTP sent. Please verify your email."
                );
            }
            if(userRegistrationRepository.findByUsername(request.getUsername()).isPresent()){
                throw new BadRequestException("Username already exists");
            }

            UserRegistration registration=new UserRegistration();
            registration.setUsername(request.getUsername());
            registration.setEmail(request.getEmail());
            registration.setPassword(passwordEncoder.encode(request.getPassword()));
            int otp=OTP_MIN + new SecureRandom().nextInt(OTP_RANGE);

            registration.setOtpHash(passwordEncoder.encode(String.valueOf(otp)));
            registration.setOtpExpiresAt(now.plusMinutes(OTP_EXPIRATION_MINUTES));
            registration.setOtpAttempts(0);
            registration.setCreatedAt(now);
            registration.setUpdatedAt(now);

            userRegistrationRepository.save(registration);

            mailService.sendEmail(registration.getEmail(),"Email verification OTP","Your OTP is :"+otp);



        return new RegisterResponse("Registration OTP sent. Please verify your email.");
    }



    public void verifyRegistration(VerifyRegistrationRequest request){
        UserRegistration registration=userRegistrationRepository.findByEmail(request.getEmail()).orElseThrow(()->new BadRequestException("Registration not found"));
        LocalDateTime now=LocalDateTime.now();

        if(registration.getOtpAttempts()>=Max_OTP_ATTEMPTS){
            throw new BadRequestException("Too many invalid OTP attempts");
            
        }

        if(registration.getOtpExpiresAt().isBefore(now)){
            throw new BadRequestException("OTP has expired");
        }

        if(!passwordEncoder.matches(request.getOtp(),registration.getOtpHash())){
            registration.setOtpAttempts(registration.getOtpAttempts()+1);
            userRegistrationRepository.save(registration);
            throw new BadRequestException("Invalid OTP");
        }

        User user =new User();
        user.setUsername(registration.getUsername());
        user.setEmail(registration.getEmail());
        user.setPassword(registration.getPassword());
        user.setRole("USER");
        userRepository.save(user);

        userRegistrationRepository.delete(registration);


    }

    public String  forgotPassword(ForgotPasswordRequest request){
        Optional<User> userOptional=userRepository.findByUsername(request.getUsername());
        if(userOptional.isEmpty()){
            return "If the account exists ,a password reset OTP has been sent.";
        }
        
        User user=userOptional.get();
        int otp=OTP_MIN+new SecureRandom().nextInt(OTP_RANGE);
        PasswordResetOtp resetOtp=passwordResetOtpRepository.findByUser(user).orElse(new PasswordResetOtp());
        if(resetOtp.getLastSentAt()!=null && resetOtp.getLastSentAt().plusSeconds(OTP_COOLDOWN_SECONDS).isAfter(LocalDateTime.now())){

            throw new BadRequestException("Please wait before requesting another OTP");

        }
         resetOtp.setLastSentAt(LocalDateTime.now());
        resetOtp.setOtpHash(passwordEncoder.encode(String.valueOf(otp)));
        resetOtp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        resetOtp.setVerified(false);
        resetOtp.setUser(user);
        resetOtp.setAttempts(0);
        passwordResetOtpRepository.save(resetOtp);
        mailService.sendEmail(user.getEmail(), "Password reset OTP","Your OTP is:"+otp);
        return "If the account exists ,a password reset OTP has been sent.";
    }

        public void verifyOtp(VerifyOtpRequest request){
           User user=userRepository.findByUsername(request.getUsername()).orElseThrow(()->new UserNotFoundException("User not found"));
            PasswordResetOtp resetOtp=passwordResetOtpRepository.findByUser(user).orElseThrow(()->new BadRequestException("OTP not found"));

            if(resetOtp.getAttempts()>=Max_OTP_ATTEMPTS){
                throw new BadRequestException("Too many invalid OTP attempts");
            }

            if(resetOtp.getExpiresAt().isBefore(LocalDateTime.now())){
                throw new BadRequestException("OTP EXPIRED");
            }

           if(!passwordEncoder.matches(request.getOtp(),resetOtp.getOtpHash())){
             resetOtp.setAttempts(resetOtp.getAttempts()+1);
             passwordResetOtpRepository.save(resetOtp);
            throw new BadRequestException("Invalid OTP");
           }
           resetOtp.setVerified(true);

           passwordResetOtpRepository.save(resetOtp);
        }


        public void resetPassword(ResetPasswordRequest request){

            User user=userRepository.findByUsername(request.getUsername()).orElseThrow(()->new UserNotFoundException("user not found"));

            PasswordResetOtp resetOtp=passwordResetOtpRepository.findByUser(user).orElseThrow(()-> new BadRequestException("OTP not found"));

            if(resetOtp.getExpiresAt()==null || resetOtp.getExpiresAt().isBefore(LocalDateTime.now())){
                throw new BadRequestException("OTP has expired");
            }

            if(!resetOtp.isVerified()){
                throw new BadRequestException("OTP not verified");

            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setTokenVersion(user.getTokenVersion()+1);
            userRepository.save(user);

              passwordResetOtpRepository.delete(resetOtp);

           
            
        }


    
}
