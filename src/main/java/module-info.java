
module com.fabio {
    requires javafx.controls;
    requires javafx.fxml;
    
    // Exportar paquetes para JavaFX
    exports com.fabio;
    exports com.fabio.controladores to javafx.fxml;
    exports com.fabio.modelo;
    
    // Abrir paquetes para reflexión de JavaFX
    opens com.fabio.controladores to javafx.fxml;
}