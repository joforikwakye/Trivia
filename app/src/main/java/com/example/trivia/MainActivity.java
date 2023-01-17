package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private List<Question> questions;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(this);

        binding.highestScore.setText(MessageFormat.format("Highest Score: {0}", prefs.getHighestScore()));
        currentQuestionIndex = prefs.getState();
        questions = new Repository().getQuestions(questionArrayList -> {
            updateQuestion();
            binding.questionOutOf.setText(MessageFormat.format("Question: {0}/{1}", currentQuestionIndex, questions.size()));

        });

        binding.nextButton.setOnClickListener(view -> getNextQuestion());

        binding.trueButton.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestion();
        });

        binding.falseButton.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestion();
        });
    }

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questions.size();
        binding.questionOutOf.setText(MessageFormat.format("Question: {0}/{1}", currentQuestionIndex, questions.size()));
        updateQuestion();
    }

    private void checkAnswer(boolean userAnswer) {
        boolean answer = questions.get(currentQuestionIndex).isAnswerTrue();
        String messageId;
        if (userAnswer == answer) {
            messageId = "Correct";
            addPoints();
            fadeAnimation();
        } else {
            messageId = "Incorrect";
            deductPoints();
            shakeAnimation();
        }

        Snackbar.make(binding.cardView, messageId, Snackbar.LENGTH_SHORT).show();
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        binding.score.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
    }

    private void deductPoints() {
        if (scoreCounter > 0) {
            scoreCounter -= 100;
            score.setScore(scoreCounter);
            binding.score.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

        } else {
            scoreCounter = 0;

        }
        score.setScore(score.getScore());

    }


    private void updateQuestion() {
        binding.showQuestion.setText(questions.get(currentQuestionIndex).getAnswer());
    }

    private void shakeAnimation() {
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        binding.cardView.setAnimation(shakeAnimation);

        shakeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.showQuestion.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.showQuestion.setTextColor(Color.WHITE);
                getNextQuestion();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeAnimation() {
        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation);
        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.showQuestion.setTextColor(Color.GREEN);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.showQuestion.setTextColor(Color.WHITE);
                getNextQuestion();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onPause() {
        prefs.setHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}