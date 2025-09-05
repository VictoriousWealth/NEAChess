This document provides a comprehensive explanation of the **NEAChess** codebase, a complete chess game developed in Java using the Swing GUI framework.

### Project Overview

**NEAChess** is a desktop chess application. Based on the name "NEA" (Non-Examined Assessment), it's likely a computer science student project. The application features a graphical chessboard, standard chess piece movement, capture logic, turn tracking, and special rules like check, checkmate, castling, and pawn promotion. It also includes a theming system to change the board's appearance.

The core of the application is built with **Java's Swing library** for the user interface and standard Java for the game logic. The logic is event-driven, responding to user clicks on the chessboard tiles.

---

### Codebase Structure

The project is organized into several packages, separating the game logic, resources, and development tools.

* `./NEAChess/src`: This is the main directory for all Java source code (`.java` files) and image resources.
    * `All/Game/`: Contains all the classes related to the game's user interface and core logic.
    * `All/Resources/`: Holds image assets (`.gif`, `.png`) and classes that manage resources like colors and themes.
    * `All/DevelopmentTools/`: Contains utility classes that may not be directly used in the final game but were used during development.
* `./out/`: This is the output directory where the integrated development environment (IDE), likely IntelliJ IDEA, places the compiled Java bytecode (`.class` files). Its structure mirrors the `src` directory.
* `NEAChess.iml`: This is a project file for the **IntelliJ IDEA IDE**, containing module configurations and dependencies.

---

### Core Components Breakdown

Here is a detailed breakdown of each Java file and its purpose.

#### `All/Game` Package (Core Logic and UI)

* `LoadingScreen.java` & `PlayButtonScreen.java`
    * **Purpose**: These classes create the initial splash screens for the application.
    * **Functionality**: `LoadingScreen` is the entry point (`main` method). It displays a loading bar with a simple color-changing animation. Once complete, it opens the `PlayButtonScreen`, which presents a "Play" button to the user. Clicking this button disposes the screen and launches the main game window (`Main.java`).

* `Main.java`
    * **Purpose**: This class represents the main game window that contains the chessboard and side panels.
    * **Functionality**: It sets up the main `JFrame` and adds three primary components:
        1.  A `ChessBoardArea` instance.
        2.  A `TaliBoardForMoves` to display a log of all moves made.
        3.  A `TaliBoardForCapturedPieces` to show which pieces have been captured.
    * It also creates a "Settings" menu in the menu bar, which allows for toggling a "Developer Mode" (making the `NextTurnButton` visible) and changing the visual theme of the game.

* `ChessBoardArea.java`
    * **Purpose**: This class acts as the container for the 8x8 grid of chess tiles.
    * **Functionality**: It uses a `GridLayout` to arrange 64 `ChessTile` objects. It's responsible for the initial setup of the board, creating all the tiles, and calling the method to place the chess pieces in their starting positions.

* `ChessTile.java`
    * **Purpose**: **This is the most critical class in the project.** It represents a single tile on the chessboard and contains the vast majority of the game's logic.
    * **Functionality**:
        * **UI Element**: Each `ChessTile` is a `JButton`, making it clickable.
        * **State Management**: It holds its own state, such as whether it's occupied, which piece is on it (`Piece` enum), the piece's color (`PieceColor` enum), and if it's selected.
        * **Global Game State**: It uses **static variables** to manage the global state of the game, including whose turn it is (`isBlackTurn`), the positions of both kings (`blackKingPosition`, `whiteKingPosition`), and a list of all 64 `ChessTile` objects (`chessTiles`).
        * **Event Handling**: Its `actionPerformed` method is the heart of the game. It handles all user clicks to select a piece, show possible moves, move a piece, or capture an opponent's piece.
        * **Rules Engine**: It contains numerous methods (`showPossibleMoves`, `showPossibleKills`, `kingIsInCheck`, etc.) that implement the rules of chess for every piece, including complex logic for check, checkmate, castling, and pawn promotion.

* `Piece.java` & `PieceColor.java`
    * **Purpose**: These are simple `enum` types.
    * **Functionality**: `Piece` defines all possible chess pieces (e.g., `BPAWN`, `WKING`) and a `nopiece` state. `PieceColor` defines the colors `black`, `white`, and `noColor`. Using enums makes the code more readable and type-safe.

* `TaliBoardForMoves.java` & `TaliBoardForCapturedPieces.java`
    * **Purpose**: These are UI components that provide information to the player.
    * **Functionality**:
        * `TaliBoardForMoves`: A `JTextArea` inside a `JScrollPane` that logs every move made, converting piece types and tile numbers into standard chess notation (e.g., "WPAWN: E2->E4").
        * `TaliBoardForCapturedPieces`: A `JTextArea` that displays a running count of all captured pieces for both sides. It updates whenever a piece is taken.

* `NextTurnButton.java`
    * **Purpose**: A debugging tool enabled via the "Developer Mode" in the settings.
    * **Functionality**: When clicked, it manually advances the game to the next turn and resets the board's appearance, which is useful for testing without making a valid move.

#### `All/Resources` Package (Assets and Theming)

* `ImageFields.java`
    * **Purpose**: This class centralizes all the image assets for the chess pieces.
    * **Functionality**: It defines a `static final Icon` for each piece (e.g., `B_PAWN_IMAGE`). **Note**: It uses hardcoded absolute file paths (`C:\\Users\\efeon\\...`), which means the program will only run on the developer's specific machine without modification.

* `Palette.java` & `THEMES_ENUM.java`
    * **Purpose**: These enums support the theming system.
    * **Functionality**: `Palette` defines the different types of colors used in a theme (e.g., `lightTileColor`, `selectedTileColor`). `THEMES_ENUM` defines the available themes (e.g., `THEME1`, `THEME2`).

* `THEMES.java`
    * **Purpose**: This class defines the specific colors for each theme.
    * **Functionality**: It extends a `HashMap` and, based on the `THEMES_ENUM` passed to its constructor, it populates itself with `Color` objects corresponding to each `Palette` entry. This allows the game to easily switch color schemes by creating a new `THEMES` object.

#### `All/DevelopmentTools` Package (Utilities)

* `Sort.java`
    * **Purpose**: Provides a `quickSort` algorithm for an `ArrayList` of integers.
    * **Functionality**: This class appears to be an auxiliary utility. It is not called or used by any of the core chess game classes and is likely a leftover from a separate programming exercise.

---

### How It Works: Execution Flow

1.  **Launch**: The program starts from `LoadingScreen.main()`. A loading bar is shown.
2.  **Menu**: The `PlayButtonScreen` appears. The user clicks "Play".
3.  **Game Setup**: The `Main` window is created. It initializes the `ChessBoardArea` and the side panels. `ChessBoardArea` creates 64 `ChessTile` buttons and places the pieces in their starting positions.
4.  **Gameplay Loop**: The game is now entirely driven by events in `ChessTile.actionPerformed()`:
    * A player clicks a tile with their piece. The tile is highlighted, and `showPossibleMoves()` and `showPossibleKills()` are called to highlight valid destination squares.
    * The player then clicks a highlighted destination square.
    * The `actionPerformed` logic handles the piece movement or capture, updates the piece on both tiles, and increments the piece's move counter.
    * The turn switches to the other player (`isBlackTurn` is toggled).
    * After each move, `kingIsInCheck()` is called to see if the move has put the opposing king in check. Checkmate is determined by a complex condition that checks if the king is in check and has no legal moves.
    * The `TaliBoard`s are updated to reflect the move and any captures.

---

### Key Design Choices & Potential Improvements

* **Strengths ✨**
    * **Good UI Separation**: The use of `Main`, `ChessBoardArea`, and `ChessTile` separates the window, the board container, and the individual tiles well.
    * **Effective Theming**: The system using `THEMES`, `THEMES_ENUM`, and `Palette` is a clean and extensible way to manage UI appearance.
    * **Use of Enums**: `Piece` and `PieceColor` enums make the code clear and prevent errors from using raw strings or integers.

* **Areas for Improvement 💡**
    * **Over-reliance on Static State**: The entire game state is managed through `static` variables in `ChessTile`. This is a significant design flaw because it tightly couples all `ChessTile` objects and makes it impossible to manage more than one game instance. A better approach would be to create a `GameState` or `Board` class to hold this information and pass it to the objects that need it.
    * **God Class (`ChessTile`)**: The `ChessTile` class does too much (UI, event handling, rules logic, state management). This violates the Single Responsibility Principle. The move logic for each piece could be abstracted into its own class (e.g., `PawnMoveStrategy`, `RookMoveStrategy`) to make the code cleaner and easier to manage.
    * **Hardcoded File Paths**: The image paths in `ImageFields.java` are absolute. This should be changed to use Java's resource loading system (`getClass().getResource()`) to make the application portable.
    * **Code Duplication**: There is significant code duplication, especially within the move/kill logic and check-detection methods. These could be refactored into more generic, reusable functions.
