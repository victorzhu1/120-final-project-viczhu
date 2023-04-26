=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: viczhu
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D arrays: The chess board is a 2x2 array, where [y][x] represents the location
  of pieces on the board. [0][0] is the top left, and [7][7] is the bottom right. This
  is appropriate because it is an efficient structure to store the information in the
  board in, especially because a lot of the operations being performed on the board
  require iteration, and when considering pieces like rooks that move straight across
  rows and columns, the 2D array makes it simple.

  2. Complex game logic: Aside from having the pieces move the way they do, multiple
  complex logic features were included such as en passant and castling, which take into
  account previous states of the board and very specific movements. There is also logic
  behind legal and illegal moves, implemented with methods that check whether a move would
  leave a king attacked, and those moves are adjusted accordingly based on that logic.

  3. Inheritance and subtyping: I made an interface "Piece" and used it as a supertype of the
  individual piece types. This was an appropriate use of the concept because it helped to
  keep the code organized and reusable, and I could work with "Piece" objects in other classes
  and cast to individual piece types when needed, which was helpful when I was making generic
  methods.

  4. JUnit testable component: Because a lot of the rules of chess are tricky and depend
  on the state of the board, I utilized testing to make sure moves that were illegal were not
  executed when I called them, and vice versa with legal moves. I also tested mechanics that
  would have been tricky just by looking at my code, such as castling and en passant.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  1. Piece classes (6 total): These classes define the properties and methods
  of individual pieces. Their function is to allow other higher level classes
  manipulate the pieces, and making a class for them allowed for pieces to be
  stored as objects inside the 2D arrays used.
  2. Chess: This class is an object that represents the chess board model. Its
  purpose is to maintain the state of the board and is responsible for the
  movement of the pieces in the game. It also has many methods that help define
  what moves are valid, and also contains the logic for the computer's move making.
  3. GameBoard: This instantiates the model defined in the Chess class. It is
  responsible for the controller and view portion of the game, and uses the Chess
  model as the board that it can control with mouse clicks. It also draws the actual
  board based on the state of it. Also, this class has all the instructions
  and options that show up for the user to interact with before the game starts.
  3. RunChess - This runs the program and handles the widgets that the user sees
  and interacts with in the GUI.
  4. Game - This runs the program.
  5. Unit test classes (2 total) - ChessMethodsTest tests the methods that are in
  the chess class, and moveValiditiesTest tests if pieces move as expected.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  It was very tricky to implement en passant and castling, as everything before
  depended only on the positions of other pieces, but these two features
  depended on the past state of the entire game (can only castle if king and
  corresponding rook have never moved, etc.)


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
I think that the lines between the Chess class and the GameBoard class got kind
of grey, and that they started handling responsibilities of each other throughout
the process of making my game. For example, both of them handle the events that occur
when a game ends, which should have been more encapsulated within just the Chess class.
Instead, I ended up having to make a lot of getters and setters for the two classes
to interact. This is all stuff I would change.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.
Images: https://commons.wikimedia.org/wiki/Category:PNG_chess_pieces/Standard_transparent
Helpful information about making a computer to play against: https://www.chessprogramming.org/Main_Page