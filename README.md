# V2 Assessment App

This is a dynamic form-driven Android application developed in Java. It fetches form data from an API and renders various form elements like radio buttons, text inputs, checkboxes, dropdowns, and camera placeholders based on the JSON schema. The submitted answers are saved in a local Room database and displayed in a separate view.

Features

- Dynamically renders form questions from a JSON API.
- Supports multiple input types:
  - Multiple Choice (Radio Buttons)
  - Text Input
  - Number Input
  - Checkboxes
  - Dropdown (Spinner)
  - Camera (placeholder for future implementation)
- Navigation using `referTo` and `skip` logic.
- Persists user responses using Room Database.
- Displays submitted responses in a separate simple decorated activity.
- Robust state handling for invalid input (e.g., dropdown default option).
- Retrofit-based API integration.


Tech Stack

 Layer         Technology          

 Language      Java                 
 Architecture  MVVM (loosely)       
 UI            View Binding, XML    
 API Client    Retrofit             
 Local DB      Room                 
 JSON Parsing  Gson                 


Dependencies Used

- Retrofit – for REST API communication
- Room – for local database storage
- Gson – for JSON parsing
- View Binding – for safe access to views
- ExecutorService – for background DB operations


