# AlejandriaApp

Alejandria is designed to connect publishers and readers. The app features a subscription system that integrates with the MercadoPago API, allowing users to create and manage monthly subscriptions across various plans. It utilizes MVVM (Model-View-ViewModel) architecture, integrates with MercadoPago and Google Books APIs, and incorporates Dagger/Hilt, Retrofit, Firebase, Flow, coroutines, LiveData, and more!

## Demo

### Splash Screen

<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/9c58c6dc-51ac-42ab-96a6-ef0177569aed" alt="splash" style="width: 48%;">

### Home

<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/cc724887-4543-4135-add3-9c90db3740d5" alt="home" style="width: 48%;">

### Search a book

<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/168ea229-c27e-4995-9811-72cb57f2481f" alt="search" style="width: 48%;">

### Subscribe

<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/90ccd36c-bf57-4674-8c00-8e2d20aa470c" alt="subscription" style="width: 48%;">

### Reserve a book

<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/f9727348-dd50-4114-a743-8d43c0a46c26" alt="reserve" style="width: 48%;">

### Register

<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/a2508552-dffb-47e1-a3a8-14bce5780e75" alt="register" style="width: 48%;">

### Login

<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/34f23141-984c-40ed-b7a0-88a43b932bca" alt="login" style="width: 48%;">


### Recover password
<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/178febd1-a814-483c-b265-70616fb21cb5" alt="recover" style="width: 48%;">

### Dark mode
<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/a358d035-48b5-464c-a2e1-65b4b5e1bc41" alt="dark" style="width: 48%;">
<img src="https://github.com/mandelbaummatias/AlejandriaApp/assets/105255748/804d76f1-c487-45da-b077-63e56f01bcf0" alt="dark2" style="width: 48%;">

## Key Features

### Core Functionality
- 🎨 **Material Design 3:** Incorporates Google's Material Design principles for a modern and visually appealing UI.
- 📏 **Constraint Layout:** Enables the creation of complex layouts with a flat view hierarchy.
- ⚙️ **Preference Library:** Simplifies the implementation of app settings with a convenient preference API.

### Testing
- 🧪 **JUnit and MockK:** Facilitates unit testing and mocking for robust code verification.

### UI Components
- 🔄 **RecyclerView:** Displays large data sets efficiently in a scrolling view.
- 🛠️ **Activity-KTX:** Enhances the conciseness and readability of code related to activities.

### Lifecycle Management
- 🧬 **ViewModel and LiveData:** For UI-related data, ensuring a seamless and responsive interaction between the UI and underlying data sources.

- 🌊 **Flow:** Empowers asynchronous data flow with a reactive approach, enhancing flexibility and responsiveness in networking tasks.

### Navigation
- 🗺️ **Navigation Components:** Simplifies navigation and the implementation of common design patterns.

### Networking
- 🌐 **Retrofit and OkHttp:** Handles network operations, facilitating communication with external APIs.

### Browser
- 🌐 **Browser:** Allows seamless integration with in-app web browsing.

### Dependency Injection
- 💉 **Dagger Hilt:** Implements dependency injection for improved code modularity and testability.

### Firebase
- 📊 **Firebase Analytics:** Gathers insights into user behavior and engagement.
- 🔒 **Firebase Authentication:** Simplifies user authentication processes.
- 📄 **Firebase Firestore:** Provides a scalable and cloud-based NoSQL database.

### Image Loading
- 🖼️  **Coil Image Loader:** Efficiently loads and displays images in the app.

### Search
- 🔍 **Algolia Search:** Facilitates powerful and efficient search functionality within the app.

### HTTP Client
- 🚀 **Ktor Client:** Offers a flexible and asynchronous HTTP client for efficient communication with servers.


  

## You will need...

🔑 Access token from your Mercado Pago Developers account, Algolia API Key and Application ID, google-services.json from Firebase for Firestore and Authentication.

👥 A seller user and a buyer user account from Mercado Pago.
