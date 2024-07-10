# traffic-control-game
Traffic simulation game created with Java FX

A Traffic Control Game game developed using JavaFX. The game allows players to manage traffic flow within a city map by controlling traffic lights. Players must ensure cars reach their destinations while avoiding collisions.

## Table of Contents

- [Overview](#overview)
- [Installation](#installation)
- [Gameplay](#gameplay)
- [Screenshots](#screenshots)
- [Features](#features)
  - [Game Environment](#game-environment)
  - [Classes](#classes)
    - [Metadata](#metadata)
    - [Car](#car)
    - [RoadTile](#roadtile)
    - [Building](#building)
    - [TrafficLight](#trafficlight)
  - [Level Parsing](#level-parsing)
  - [Select Custom Level](#select-custom-level)
  - [Car Movement](#car-movement)
  - [Traffic Control](#traffic-control)
  - [Win and Lose Conditions](#win-and-lose-conditions)
- [Development](#development)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Overview

Traffic Control Simulator is a game where players manage traffic lights to ensure a smooth flow of traffic within a city. The objective is to guide cars to their destinations while preventing collisions and minimizing traffic jams. 

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/traffic-control-simulator.git

2. Navigate to the project directory:
   ```sh
    cd traffic-control-simulator

3. Ensure you have Java and JavaFX installed on your machine.
4. Compile the Java files:
   ```sh
   javac -cp path/to/javafx-sdk/lib/*:. main.java
5. Run the application:
    ```sh
    java -cp path/to/javafx-sdk/lib/*:. main

## Gameplay
* Start the game and select a level.
* Control the traffic lights by clicking on them to change their colors.
* Monitor the flow of cars and avoid collisions.
* Reach the win conditions by successfully guiding the required number of cars to their destinations.

## Screenshots

![img1](https://github.com/devran-hub/traffic-control-game/assets/73471656/3d21b145-014a-4ceb-84d8-1483b7d1967e)
![img2](https://github.com/devran-hub/traffic-control-game/assets/73471656/47dfaad9-dbb3-4c7d-a354-2e118779c284)
![img3](https://github.com/devran-hub/traffic-control-game/assets/73471656/e6617622-4716-4390-9bf4-883215fbef26)
![img4](https://github.com/devran-hub/traffic-control-game/assets/73471656/a58f8f13-ba70-4da0-b1ce-a916a285a5a0)
![img5](https://github.com/devran-hub/traffic-control-game/assets/73471656/03958ccc-9c04-484d-9954-e89ed6801d20)
![img6](https://github.com/devran-hub/traffic-control-game/assets/73471656/b9856c96-49f2-49de-a9a7-569e9fe9c324)


## Features

### Game Environment
* **RoadTile**: Represents different types of roads, including straight, curved, four-way intersections, and three-way intersections. Each type can be rotated to fit the required direction.
* **Building:** Represents different types of buildings that act as starting and destination points for the cars. There are three types of buildings with four different colors.
* **TrafficLight:** Represents traffic lights that can change color when clicked, controlling car movement based on their color (red or green).
### Classes

#### Metadata

The Metadata class represents base of the map which road, traffic lights, buildings and cars placed in

#### Car

The Car class represents main specifications of cars such as size, shape, path etc.

#### RoadTile

The RoadTile class represents different road types and their orientations.

#### Building

The Building class represents the starting and destination points for cars.

#### TrafficLight

The TrafficLight class manages the traffic light states and interactions with cars.

### Level Parsing
Levels are parsed from text files containing metadata, buildings, road tiles, traffic lights, and paths. This allows for easy creation and modification of game levels.

### Select Custom Level
By clicking "Select Level" button, the player can choose level to play and try to complete it

### Car Movement
Car objects are spawned from buildings and follow designated paths. Car movement involves stopping at red lights and avoiding collisions. Cars travel along paths using the PathTransition class.

### Traffic Control
Players can influence traffic flow by clicking on traffic lights to change their colors. Cars stop at red lights and proceed on green lights.

### Win and Lose Conditions
**Win Condition**: A specific number of cars must reach their destinations.

**Lose Condition**: The game ends if the number of allowed collisions is exceeded. These conditions are displayed at the top right corner of the map.

## Development

Feel free to contribute to the project by forking the repository and submitting pull requests. Ensure your code is well-documented and follows coding standards.

## Contributing

1. Fork the repository.
Create your feature branch
    ```sh
    git checkout -b feature/YourFeature
    
2. Commit your changes:

    ```sh
    git commit -m 'Add some feature'
3. Push to the branch:
    ```sh
    git push origin feature/YourFeature
4. Open a pull request.
## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Contact

For any inquiries or feedback, please contact devrimpol2000@gmail.com


