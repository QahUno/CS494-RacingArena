package Server;

import java.time.Instant;
import java.util.Random;

public class GameExpression {
    public Character[] operators = {'+', '-', '*', '/', '%'};
    public int firstOperand;
    public int secondOperand;
    public Character operator;
    public int expectedResult;
    public Instant timestamp;

    public GameExpression() {
        Random random = new Random();
        timestamp = Instant.now();
        this.operator = this.operators[random.nextInt(this.operators.length)];
        this.firstOperand = random.nextInt(-10000, 10000);
        this.secondOperand = random.nextInt(-10000, 10000);
        if(Character.compare(operator, '/') == 0 || Character.compare(operator, '%') == 0) {
        	while(this.secondOperand == 0) {
                this.secondOperand = random.nextInt(-10000, 10000);
                System.out.print(this.secondOperand);
        	}
        }

        if (operator == '+') {
            this.expectedResult = this.firstOperand + this.secondOperand;
        } else if (operator == '-') {
            this.expectedResult = this.firstOperand - this.secondOperand;
        } else if (operator == '*') {
            this.expectedResult = this.firstOperand * this.secondOperand;
        } else if (operator == '/') {
            this.expectedResult = this.firstOperand / this.secondOperand;
        } else if (operator == '%') {
            this.expectedResult = this.firstOperand % this.secondOperand;
        }
    }
    
    public String convertToString() {
    	return String.format("%d %c %d = ?", this.firstOperand, this.operator, this.secondOperand);

    }
}
