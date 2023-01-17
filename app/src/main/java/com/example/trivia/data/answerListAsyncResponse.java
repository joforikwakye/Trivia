package com.example.trivia.data;

import com.example.trivia.model.Question;

import java.util.ArrayList;

public interface answerListAsyncResponse {

     void processFinished(ArrayList<Question> questionArrayList);
}
