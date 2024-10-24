package com.example.wordlegame

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var guessInput: EditText
    private lateinit var submitButton: Button
    private lateinit var exitButton: Button
    private lateinit var pointsTextView: TextView
    private lateinit var nextButton: Button

    private var currentRow = 0
    private var points = 0
    private var wordToGuess = ""

    // List of 5-letter words
    private val wordList = listOf("APPLE", "BRAIN", "CHAIR", "TABLE", "MOUSE", "LUNCH", "GRAPE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        guessInput = findViewById(R.id.guessInput)
        submitButton = findViewById(R.id.submitButton)
        exitButton = findViewById(R.id.exitButton)
        pointsTextView = findViewById(R.id.points)
        nextButton = findViewById(R.id.nextButton)

        // Pick a random word from the list
        wordToGuess = wordList[Random.nextInt(wordList.size)]

        submitButton.setOnClickListener {
            val guess = guessInput.text.toString().uppercase()
            if (guess.length == 5) {
                checkGuess(guess)
            } else {
                Toast.makeText(this, "Enter a 5-letter word", Toast.LENGTH_SHORT).show()
            }
        }

        // Exit button functionality
        exitButton.setOnClickListener {
            showExitConfirmationDialog()
        }

        // Next button functionality
        nextButton.setOnClickListener {
            nextRound()
        }
    }

    private fun checkGuess(guess: String) {
        if (currentRow >= 6) {
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
            showRestartDialog("You've run out of guesses! The word was $wordToGuess. Do you want to restart?")
            return
        }

        for (i in guess.indices) {
            val cellId = "cell_${currentRow}${i}"
            val cell: TextView = findViewById(resources.getIdentifier(cellId, "id", packageName))
            cell.text = guess[i].toString()

            if (guess[i] == wordToGuess[i]) {
                cell.setBackgroundColor(Color.GREEN)
            } else if (wordToGuess.contains(guess[i])) {
                cell.setBackgroundColor(Color.YELLOW)
            } else {
                cell.setBackgroundColor(Color.RED)
            }
        }

        currentRow++

        if (guess == wordToGuess) {
            points += 10
            pointsTextView.text = "Points: $points"
            Toast.makeText(this, "Correct! The word is $wordToGuess", Toast.LENGTH_LONG).show()
            nextButton.visibility = Button.VISIBLE
            submitButton.visibility = Button.GONE
            guessInput.visibility = EditText.GONE
        } else if (currentRow == 6) {
            showRestartDialog("Out of tries! The word was $wordToGuess. Do you want to restart?")
        }
    }

    private fun nextRound() {
        resetGame()
        nextButton.visibility = Button.GONE
        submitButton.visibility = Button.VISIBLE
        guessInput.visibility = EditText.VISIBLE
        guessInput.text.clear()
    }

    private fun resetGame() {
        currentRow = 0
        wordToGuess = wordList[Random.nextInt(wordList.size)]

        // Reset all the cells (TextViews) in the grid
        for (i in 0..5) {
            for (j in 0..4) {
                val cellId = "cell_${i}${j}"
                val cell: TextView = findViewById(resources.getIdentifier(cellId, "id", packageName))
                cell.text = ""  // Clear text in each cell
                cell.setBackgroundColor(Color.GRAY)  // Reset the background color to black
                cell.visibility = TextView.VISIBLE   // Ensure each cell is visible
            }
        }

        // Reset the input field and buttons
        guessInput.text.clear()
        guessInput.visibility = EditText.VISIBLE
        submitButton.visibility = Button.VISIBLE
        nextButton.visibility = Button.GONE
    }


    private fun showExitConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to exit the game?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ -> finish() })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

        val alert = dialogBuilder.create()
        alert.setTitle("Exit")
        alert.show()
    }

    private fun showRestartDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Restart", DialogInterface.OnClickListener { _, _ ->
                resetGame()
                points = 0
                pointsTextView.text = "Points: $points"
                guessInput.text.clear()
                submitButton.visibility = Button.VISIBLE
                guessInput.visibility = EditText.VISIBLE
            })
            .setNegativeButton("Exit", DialogInterface.OnClickListener { _, _ -> finish() })

        val alert = dialogBuilder.create()
        alert.setTitle("Game Over")
        alert.show()
    }
}
