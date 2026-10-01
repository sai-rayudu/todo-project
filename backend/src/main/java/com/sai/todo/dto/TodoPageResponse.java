package com.sai.todo.dto;
import java.util.List;

public class TodoPageResponse {

    private List<TodoDto> todos;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private boolean hasNext;

    public TodoPageResponse(){

    }

    public TodoPageResponse(List<TodoDto> todos,int currentPage,int totalPages,long totalItems,boolean hasNext){
        this.todos=todos;
        this.currentPage=currentPage;
        this.totalPages=totalPages;
        this.totalItems=totalItems;
        this.hasNext=hasNext;
    }

    public List<TodoDto> getTodos(){
        return todos;
    }
    public void setTodos(List<TodoDto> todos){
        this.todos=todos;

    }
    public int getCurrentPage(){
        return currentPage;
    }
    public void setCurrentPage(int currentPage){
        this.currentPage=currentPage;
    }

    public int getTotalPages(){
        return totalPages;
    }
    public void setTotalPages(int totalPages){
        this.totalPages=totalPages;
    }
    public long getTotalItems(){
        return totalItems;
    }
    public void setTotalItems(long totalItems){
        this.totalItems=totalItems;
    }
    public boolean isHasNext(){
        return hasNext;
    }
    public void setHasNext(boolean hasNext){
        this.hasNext=hasNext;
    }
    
}
