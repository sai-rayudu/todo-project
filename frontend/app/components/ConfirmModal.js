"use client";
import { useState, useEffect } from "react";

export default function ConfirmModal({ isOpen, onClose, onConfirm, title, message, loading, requirePassword }) {
    const [password, setPassword] = useState("");

    // Reset password every time the modal opens
    useEffect(() => {
        if (isOpen) setPassword("");
    }, [isOpen]);

    if (!isOpen) return null;

    const handleConfirm = () => {
        onConfirm(requirePassword ? password : undefined);
    };

    const handleClose = () => {
        setPassword("");
        onClose();
    };

    return (
        <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && handleClose()}>
            <div
                className="glass animate-fadeInUp"
                style={{ width: "100%", maxWidth: 400, padding: "28px 24px" }}
            >
                {/* Icon */}
                <div
                    style={{
                        width: 48,
                        height: 48,
                        borderRadius: 12,
                        background: "rgba(239,68,68,0.15)",
                        border: "1px solid rgba(239,68,68,0.3)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        fontSize: 22,
                        marginBottom: 16,
                    }}
                >
                    🗑️
                </div>

                <h3 style={{ fontSize: 18, fontWeight: 700, color: "#f1f1f1", marginBottom: 8 }}>
                    {title || "Confirm"}
                </h3>
                <p style={{ fontSize: 14, color: "#a0a0c0", lineHeight: 1.6, marginBottom: requirePassword ? 16 : 24 }}>
                    {message || "Are you sure? This action cannot be undone."}
                </p>

                {requirePassword && (
                    <div style={{ marginBottom: 20 }}>
                        <label
                            style={{ display: "block", fontSize: 13, fontWeight: 500, color: "#c0c0e0", marginBottom: 6 }}
                        >
                            Enter your current password to confirm
                        </label>
                        <input
                            className="input-field"
                            type="password"
                            placeholder="Your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            autoComplete="current-password"
                        />
                    </div>
                )}

                <div style={{ display: "flex", gap: 10 }}>
                    <button className="btn-ghost" onClick={handleClose} style={{ flex: 1 }}>
                        Cancel
                    </button>
                    <button
                        className="btn-danger"
                        onClick={handleConfirm}
                        disabled={loading || (requirePassword && !password)}
                        style={{
                            flex: 1,
                            padding: "10px 16px",
                            opacity: loading || (requirePassword && !password) ? 0.5 : 1,
                            cursor: loading || (requirePassword && !password) ? "not-allowed" : "pointer",
                        }}
                    >
                        {loading ? "Deleting…" : "Delete"}
                    </button>
                </div>
            </div>
        </div>
    );
}
