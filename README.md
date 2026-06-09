# Social-Architect
## Requirements
 
- Java 8 or higher
- Text editor or IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)
- Command line terminal
## Project Files
 
The project consists of:
- GameState.java - Game logic and mechanics
- MainFrame.java - Entry point and window setup
- RightControlPanel.java - UI for actions and information
- SocialNetworkPanel.java - Network visualization
- StartScreen.java - Title screen
- EndScreen.java - Game over screen
- Student.java - Student data model
- StudentLoader.java - CSV file parser
- Students.csv - Student data file
All files must be in the same directory.
## Setup
 
1. Create a project folder.
2. Copy all .java files and Students.csv into the folder.
3. Open a terminal and navigate to the project folder.
## Compilation
 
Run this command:
 
```
javac *.java
```
 
This compiles all Java files. If successful, you will see no output.
## Running the Game
 
After successful compilation, run:
 
```
java MainFrame
```
 
The game window will open. You should see the start screen.
## Game Controls
 
- Click on a student node in the network to select them.
- Use the action buttons on the right panel to perform actions:
  - Host Event - Create or strengthen friendships
  - Send Gift - Increase student happiness
  - Reinforce Bond - Strengthen existing friendship
  - Break Friendship - End a bond
- Click End Day to advance to the next day.
- Game ends after 3 weeks.
## Game Rules
 
- You have 5 action points per day.
- The game lasts 15 days (3 weeks of 5 days each).
- Goal: Reach 80% average happiness and keep all students connected.
- Toxic events can occur randomly when students are unhappy (below 60%).
- Breaking a toxic bond heals damaged friendships with mutual friends.
## Data Format
 
Students.csv format:
 
```
Name,TrustFactor,Happiness(0-10),Friends
Student One,0.8,7,"Student Two:5,Student Three:3"
Student Two,0.6,6,"Student One:5,Student Four:4"
```
 
Fields:
- Name: Student name (can have spaces, wrap in quotes if needed)
- TrustFactor: 0.0 to 1.0 (how trustworthy they are)
- Happiness: 0 to 10 (starting happiness level, will be converted to 0.0-1.0)
- Friends: Comma-separated list of friend names with optional bond weights (format: "Name:Weight" or just "Name" for default weight 5.0)
## Performance
 
The game runs at 60 FPS. All calculations are instant even for large networks.
 
## Known Limitations
 
- Maximum tested with 12 students. Performance may degrade with 20+ students.
- Window must be at least 960x620 pixels to display properly.
- Changes to Students.csv only apply on game restart.
 
