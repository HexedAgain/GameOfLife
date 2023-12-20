# Game Of Life Simulator
A very simple implementation of John Conway's Game Of Life.

## To Use
Download and build the application (obviously), then ...
Set number of rows and columns, update the grid, toggle cell liveness manually and then hit the play button

### Near Future Plans
- Tidy up the UI (and fix the grid measurement, currently pretty bad)
- Add ability to randomise cell liveness as opposed to manually updating the grid
- Display the duration of the previous game (number of steps)
- Add option to pause / unpause game

### Not So Near Future Plans ...
- Add ability for grid to wrap (i.e. connecting left and right edges)
- Add ability to drag over tiles, and from this selection choose from a known library of configurations (like glider etc ...)
- Add ability to step backwards in time (with current implementation of Cells this would involve starting from initial configuration and then stepping forwards)
- Add ability to save a configuration and then load it