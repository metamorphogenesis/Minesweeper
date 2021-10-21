public enum Messages {
    TYPE_WIDTH("Type field width (" + Main.minWidth + "-" + Main.maxWidth + "): "),
    TYPE_HEIGHT("Type field height (" + Main.minHeight + "-" + Main.maxHeight + "): "),
    WIDTH_OUT_OF_RANGE("The width must be between " + Main.minWidth + "-" + Main.maxWidth + ". Set another width.\n"),
    HEIGHT_OUT_OF_RANGE("The height must be between " + Main.minHeight + "-" + Main.maxHeight + ". Set another height.\n"),
    RANDOM_WIDTH("The width generated randomly.\n"),
    RANDOM_HEIGHT("The height generated randomly.\n"),
    TYPE_MINES("Type mines quantity: "),
    MINES_OUT_OF_RANGE("The mines quantity must be more than 0 and less than cells quantity. Set another mines quantity.\n"),
    RANDOM_MINES("The mines quantity generated randomly.\n"),
    FLAG("Flag"),
    TOUCH("Touch"),
    GOT_OUT_OF_WIDTH("You got out of width. Type another x-coordinate.\n"),
    GOT_OUT_OF_HEIGHT("You got out of height. Type another y-coordinate.\n"),
    TOUCHED("You've touched this cell. Try another one.\n"),
    WON("YOU WON."),
    LOSE("YOU LOSE."),
    LINE("----------------------------"),
    NUM_IS(" - cell near the mine\n"),
    EMPTY_IS(" - empty cell"),
    FLAG_IS(" - flag"),
    MINE_IS(" - mine"),
    UNTOUCHED_IS(" - untouched cell"),
    LAST_IS(" - your last touch"),
    QUESTION("\nWould you like to play again?\n\n"),
    YES("Yes"),
    ANSWERS("[ " + YES + " ] - new game\n[ Anything else ] - exit\n"),
    SO("So? : "),
    NEW("\n" + LINE + "\nGame started\n"),
    THANKS("\nThanks for playing!"),
    RESULT("\nResult map"),
    TITLE("MINESWEEPER v2.2"),
    AUTHOR("w.shuminski"),
    GREETINGS("\tBefore you start:\n"
            + "• Type dimensions of the new field and mines quantity.\n"
            + "• Empty value means that it will be generated randomly.\n\n"
            + "\tAfter you start:\n"
            + "• Type coordinates of the cell to discover it.\n"
            + "• If you'd like to flag the cell just press Enter instead of any coordinate.\n"
            + "  In this case you'll go to the flag mode.\n"
            + "• To return to the touch mode press Enter again.\n\n"
            + "\tAt any time:\n"
            + "• Type any of Exit, Quit or End instead of any value to stop game."),
    ERROR("Something went wrong. Try again.\n");

    private final String message;

    Messages(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
