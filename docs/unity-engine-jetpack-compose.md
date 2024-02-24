# Integrating Unity Engine in your Jetpack Compose UI

This document covers two key composable functions, `UnityEngineView` and `UnityEngineScaffold`, that
enable you to display the Unity Engine instance within your Jetpack Compose application.

## `UnityEngineView` - Displaying the Unity Engine

This function provides a basic way to integrate the Unity Engine into your UI:

**Parameters**

* `modifier`: Modifier to apply to the Unity Engine view for styling and positioning.
* `onUnityEngineCreated`: An optional callback invoked when the Unity Engine is created and ready.

**Example Usage**

```kotlin
Box(modifier = Modifier.fillMaxSize()) {
  UnityEngineView(
    modifier = Modifier.size(300.dp),
    onUnityEngineCreated = { /* Optional callback */ }
  )
}
```

This will place a Unity Engine view with a size of 300dp within a `Box` that fills the entire
screen. You can customize the size and position using the `modifier` parameter.

## `UnityEngineScaffold` - Layout with Unity Engine and Content

This function offers a more structured approach for integrating the Unity Engine alongside other UI
elements:

**Parameters**

* `modifier`: Modifier for the overall layout.
* `unityEngineModifier`: Modifier specific to the Unity Engine view.
* `onUnityEngineCreated`: Optional callback for Unity Engine creation.
* `content`: The composable content to display alongside the Unity Engine.

**Example Usage**

```kotlin
Column(modifier = Modifier.fillMaxSize()) {
  UnityEngineScaffold(
    unityEngineModifier = Modifier.size(200.dp),
    content = {
      Text("Additional UI elements here")
      Button(onClick = {}) {
        Text("Interact with the Unity Engine")
      }
    }
  )
}
```

This example creates a column layout with the Unity Engine occupying the top portion (200dp) and
additional UI elements (text and button) below.

### Key Points

* Both functions handle the lifecycle management of the Unity Engine instance.
* Use `onUnityEngineCreated` to perform actions when the Unity Engine is ready, such as loading
  specific content or sending messages.
* Consider using `UnityEngineScaffold` when you need to integrate the Unity Engine with other UI
  elements in a structured layout.
* Remember to import the necessary dependencies and configure your Unity project for proper
  integration with Jetpack Compose.

This documentation provides a basic overview of these functions. For further details, refer to the
source code and explore the available documentation within your project.
