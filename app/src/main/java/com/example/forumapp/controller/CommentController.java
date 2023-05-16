package com.example.forumapp.controller;


public class CommentController {
    private static CommentController commentController;

    private CommentController(){

    }

    public static CommentController getInstance(){
        if(commentController == null) commentController = new CommentController();
        return commentController;
    }

    public static void createQuery(){

    }

}
