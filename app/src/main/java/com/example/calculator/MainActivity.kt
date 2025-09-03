package com.example.calculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var lastNumber: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showJerryStore()
        addCallBack()
    }

    private fun showJerryStore() {
        binding.buttonNavigation.setOnClickListener {
            val intent = Intent(this, JerryStoreActivity::class.java)
            startActivity(intent)
        }

    }

    fun onClickNumber(v: View) {
        val newDigit = (v as Button).text.toString()
        val oldDigits = binding.textViewCurrentResult.text.toString()
        if (newDigit == "." && oldDigits.split(" ").last().contains(".")) {
            return
        }

        val newTextNumber = updateNewTextNumber(oldDigits, newDigit)
        binding.textViewCurrentResult.text = newTextNumber

    }

    private fun updateNewTextNumber(oldDigits: String, newDigit: String): String {
        return if (oldDigits == "0" || oldDigits == "0.0") {
            newDigit
        } else {
            oldDigits + newDigit
        }
    }

    private fun addCallBack() {
        clearResult()
        clearLastCharacter()
        handleAddOperation()
        handleSubOperation()
        handleDividerOperation()
        handleMultipleOperation()
        handleModOperation()
        handleEqualOperation()
        getPreviousResult()
        handleSignNumber()

    }

    private fun handleSignNumber() {
        binding.buttonSign.setOnClickListener {
            changeSign()
        }
    }

    private fun changeSign() {
        val currentText = binding.textViewCurrentResult.text.toString()
        if (currentText == "0" || currentText == "0.0") return

        val parts = currentText.split(" ")
        val lastPart = parts.last()
        if (lastPart.toDoubleOrNull() == null) return

        val result = if (lastPart.startsWith("-")) {
            lastPart.substring(1)
        } else {
            "-$lastPart"
        }

        val newText = parts.toMutableList()
        newText[newText.size - 1] = result

        binding.textViewCurrentResult.text = newText.joinToString(" ")
    }

    private fun getPreviousResult() {
        if (!binding.textViewCurrentResult.text.isNullOrBlank()) {
            binding.textViewPreviousResult.setOnClickListener {
                binding.textViewCurrentResult.text = binding.textViewPreviousResult.text
            }
        }
    }

    private fun clearLastCharacter() {
        binding.buttonClearLast.setOnClickListener {
            if (binding.textViewCurrentResult.text.isNotEmpty()) {
                val text = binding.textViewCurrentResult.text.trim().dropLast(1)
                binding.textViewCurrentResult.text = if (text.isEmpty()) "0" else text
            }
        }
    }

    private fun handleEqualOperation() {
        binding.buttonEqual.setOnClickListener {
            try {
                val finalResult = handleResult()
                binding.textViewPreviousResult.text = binding.textViewCurrentResult.text
                val formatResult = if (finalResult == finalResult.toLong().toDouble()) {
                    finalResult.toLong().toString()
                } else {
                    finalResult.toString()
                }
                binding.textViewCurrentResult.text = formatResult
            } catch (e: Exception) {
                binding.textViewCurrentResult.text = e.message
                binding.textViewPreviousResult.text = ""
            }

        }
    }

    private fun handleModOperation() {
        binding.buttonMod.setOnClickListener {
            val oldDigits = binding.textViewCurrentResult.text.toString()
            currentOperation(Operation.MOD, oldDigits)
            updateLastNumber(oldDigits)
        }
    }

    private fun handleMultipleOperation() {
        binding.buttonMultiple.setOnClickListener {
            val oldDigits = binding.textViewCurrentResult.text.toString()
            currentOperation(Operation.MULTIPLY, oldDigits)
            updateLastNumber(oldDigits)
        }
    }

    private fun handleDividerOperation() {
        binding.buttonDivider.setOnClickListener {
            val oldDigits = binding.textViewCurrentResult.text.toString()
            currentOperation(Operation.DIVIDE, oldDigits)
            updateLastNumber(oldDigits)
        }

    }

    private fun handleSubOperation() {
        binding.buttonSub.setOnClickListener {
            val oldDigits = binding.textViewCurrentResult.text.toString()
            currentOperation(Operation.SUB, oldDigits)
            updateLastNumber(oldDigits)
        }
    }

    private fun handleAddOperation() {
        binding.buttonAdd.setOnClickListener {
            val oldDigits = binding.textViewCurrentResult.text.toString()
            currentOperation(Operation.PLUS, oldDigits)
            updateLastNumber(oldDigits)
        }
    }

    private fun currentOperation(operation: Operation, oldDigits: String) {
        if (!endsWithOperator(oldDigits)) {
            val newText = when (operation) {
                Operation.PLUS -> "$oldDigits + "
                Operation.SUB -> "$oldDigits - "
                Operation.MULTIPLY -> "$oldDigits * "
                Operation.DIVIDE -> "$oldDigits / "
                Operation.MOD -> "$oldDigits % "
            }
            binding.textViewCurrentResult.text = newText

        }

    }

    private fun updateLastNumber(oldDigits: String) {
        val number = oldDigits.trim().split(" ").first()
        lastNumber = number.toDouble()
    }

    private fun clearResult() {
        binding.buttonAc.setOnClickListener {
            binding.textViewCurrentResult.text = "0"
        }
    }


    private fun handleResult(): Double {
        val currentText = binding.textViewCurrentResult.text.toString()
        val parts = currentText.split(" ").filter { it.isNotEmpty() }

        if (parts.isEmpty()) return 0.0
        var result = parts.first().toDouble()
        if (parts.size == 2 && parts.last() == "%") {
            return result / 100
        }

        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<String>()
        checkParts(parts,numbers,operators)
        handleHighPriorityOperation(numbers,operators)
        return finalResult(numbers,operators)

    }

    private fun checkParts(
        parts: List<String>,
        numbers: MutableList<Double>,
        operators: MutableList<String>
    ) {
        var currentPart = 0
        while (currentPart < parts.size) {
            if (currentPart % 2 == 0) numbers.add(parts[currentPart].toDouble()) else operators.add(
                parts[currentPart]
            )
            currentPart++
        }
    }

    private fun finalResult(
        numbers: MutableList<Double>,
        operators: MutableList<String>
    ): Double {
        var result = numbers[0]
        for (currentIndex in operators.indices) {
            val nextNumber = numbers[currentIndex + 1]
            when (operators[currentIndex]) {
                "+" -> result += nextNumber
                "-" -> result -= nextNumber
            }
        }
        return result
    }

    private fun handleHighPriorityOperation(numbers: MutableList<Double>, operators: MutableList<String>) {
        var currentIndex = 0
        while (currentIndex < operators.size) {
            if (operators[currentIndex] in listOf("*", "/", "%")) {
                val left = numbers[currentIndex]
                val right = numbers[currentIndex + 1]
                val operator = operators[currentIndex]
                val result = when (operator) {
                    "*" -> left * right
                    "/" -> if (right != 0.0) left / right else throw Exception("cant divide by zero")
                    "%" -> (left / 100) * right
                    else -> return
                }
                numbers[currentIndex] = result
                numbers.removeAt(currentIndex + 1)
                operators.removeAt(currentIndex)
            } else {
                currentIndex++
            }

        }
    }

    private fun endsWithOperator(text: String): Boolean {
        return text.endsWith(" + ") || text.endsWith(" - ") ||
                text.endsWith(" * ") || text.endsWith(" / ") ||
                text.endsWith(" % ") || text.endsWith(" . ")
    }

}