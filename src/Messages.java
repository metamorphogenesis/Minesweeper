public enum Messages {
    TYPE_WIDTH("Type field width (1-100): "),
    TYPE_HEIGHT("Type field height (1-100): "),
    WIDTH_OUT_OF_RANGE("The width must be between 1-100. Set another width.\n"),
    HEIGHT_OUT_OF_RANGE("The height must be between 1-100. Set another height.\n"),
    RANDOM_WIDTH("The width generated randomly.\n"),
    RANDOM_HEIGHT("The height generated randomly.\n"),
    TYPE_MINES("Type mines quantity: "),
    MINES_OUT_OF_RANGE("The mines quantity must be more than 0 and less than cells quantity. Set another mines quantity.\n"),
    RANDOM_MINES("The mines quantity generated randomly.\n"),
    FLAG("Flag"),
    TOUCH("Touch"),
    GOT_OUT_OF_WIDTH("You got out of the width. Type another x-coordinate.\n"),
    GOT_OUT_OF_HEIGHT("You got out of the height. Type another y-coordinate.\n"),
    TOUCHED("You've touched this cell. Try another one.\n"),
    WON("\nYOU WON."),
    LOSE("\nYOU LOSE."),
    LINE("----------------------------"),
    DOUBLE_LINE("\n============================"),
    NUM_IS(" - cell near the mine\n"),
    EMPTY_IS(" - empty cell"),
    FLAG_IS(" - flag"),
    MINE_IS(" - mine"),
    UNTOUCHED_IS(" - untouched cell"),
    LAST_IS(" - your last touch"),
    QUESTION("\nWould you like to play again?\n\n"),
    YES("Yes"),
    ANSWERS("[ " + YES + "] - new game\n[ Anything else ] - exit\n"),
    SO("So? : "),
    NEW(LINE + "\nGame started\n"),
    THANKS("\nThanks for playing!"),
    RESULT("\nResult map"),
    MINES("\nMines map"),
    GREETINGS(  "MINESWEEPER v1.0\n" +
                "w.shuminski\n\n" +
                "Type dimensions of the new field and mines quantity.\n" +
                "Negative value means that the value will be generated randomly.\n" +
                "Type coordinates of the cell to discover it.\n" +
                "If you'd like to flag the cell type \"" + FLAG.toString().toLowerCase() + "\" instead of any coordinate.\n" +
                "In this case you'll go to the flag mode. To return to the discovery mode just press Enter."),
    ERROR("Something went wrong. Try again.\n");

    private String message;

    Messages(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
