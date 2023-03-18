# Resources used to do the project
- [Standard Bottom Sheet Material.io Android Studio](https://www.youtube.com/watch?v=3vMtHKniOqI)
- [Getting Started with cameraX](https://developer.android.com/codelabs/camerax-getting-started#1)
- [View Binding](https://developer.android.com/topic/libraries/view-binding)
    - Basicaly makes writing code that interacts with views easier to write. It creates a **ViewBinding** class for each **Layout File** we have


# Note on the Architecture of the Project
- we have a MainActivity file, and fragements for each section
- The camera preview will be located in the MainActivity for performance issues 
  - like that we wont have to initialise it each time we change from colorDetectionFragment to HusShiftFragment
- each fragment is handled by 2 classes
  - one for interractinf with the view
  - the other as a Model for data and whatnot