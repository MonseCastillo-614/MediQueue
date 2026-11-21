<div align="center">

 # UNIVERSIDAD FIDÉLITAS


**Curso: Estructura de Datos**

# MEDIQUEUE

### Sistema de Gestión Hospitalaria

**AVANCE 1**

---

### INTEGRANTES

| Nombre completo | Correo institucional |
|---|---|
| CASTILLO ARAGON MONSERRATH | mcastillo50644@ufide.ac.cr |
| HERNANDEZ SEGNI JOSE DANIEL | hjose90991@ufide.ac.cr |
| ALLISON EDELL ALEGRIA | aalegria50755@ufide.ac.cr |
| RAMOS RODRIGUEZ DHAYAN JESUS | dramos30655@ufide.ac.cr |

---

**Profesor:** Kenneth Artavia A.

**Costa Rica, 2026**

</div>

---

Distribución del trabajo — Avance 1

Persona 1: CASTILLO ARAGON MONSERRATH

Responsabilidad: Configuración y usuarios

Configuración inicial de la sede hospitalaria.

Definición de salas médicas, consultorios y salas de emergencia.

Inicio de sesión y gestión de usuarios.

Trabajo realizado:

En mi parte desarrollé la configuración de la sede hospitalaria, intentando hacerla lo más personalizable posible. Se puede elegir la cantidad de salas médicas, consultorios y salas de emergencia, además de seleccionar cuáles serán los espacios preferenciales y la sala de emergencia crítica. También hice el inicio de sesión y las opciones para registrar, consultar, editar y eliminar usuarios. Agregué la posibilidad de modificar la configuración de la sede o volver a configurarla desde cero.

Además, adelanté un poco de unas de las parte del trabajo de la persona 2, creando la función para guardar y recuperar la configuración y los usuarios mediante config.json. Lo hice para asegurar que mi parte funcionara correctamente y evitar errores relacionados con el guardado y la recuperación de los datos, así ahorrándole un poco de trabajo al encargado de persona 2.

Persona 2: HERNANDEZ SEGNI JOSE DANIEL

Responsabilidad: Persistencia de datos

Guardar y cargar la configuración desde config.json.

Guardar y recuperar los tiquetes pendientes desde tiquetes.json.

Coordinar la persistencia de los datos con las demás clases.

Trabajo realizado:
En mi parte desarrollé el módulo de persistencia de datos del sistema MediQueue, creando las clases necesarias para guardar y recuperar información mediante archivos JSON. Implementé funciones para leer y escribir los archivos config.json y tiquetes.json, además de validar su contenido y manejar posibles errores durante el almacenamiento. También trabajé en la estructura de nodos para interpretar los datos JSON y en una interfaz que permitirá conectar estas funciones con las demás clases del proyecto.



Persona 3: RAMOS RODRIGUEZ DHAYAN JESUS

Responsabilidad: Pacientes y tiquetes

Crear las clases Paciente y Tiquete.

Definir los atributos y métodos necesarios según el enunciado.

Incluir los datos del paciente, las fechas, la prioridad y el tipo de atención.

Trabajo realizado:
En la clase de Paciente.java realice una sección para guardar los datos básicos de cada paciente (nombre, cédula, edad y tipo de seguro), asegurándonos de que la información esté completa y correcta.
Por otro lado la clase Tiquete.java creamos el tiquete que se le entrega a cada persona, el cual incluye un número de orden, la hora en que llegó, qué tan urgente es su caso (desde crítico hasta bajo), el tipo de atención que requiere y si le corresponde un trato preferencial o normal. Con esto, el sistema puede organizar la fila automáticamente para atender primero a quienes más lo necesitan.


Persona 4: ALISSON EDELL ALEGRIA

Responsabilidad: Colas de prioridad e integración

Implementar las colas dinámicas de prioridad según lo visto en clase.

Organizar los tiquetes según las reglas del enunciado.

Integrar y probar el registro de pacientes con las demás clases.

Trabajo realizado:
Implementé las colas dinámicas de prioridad y su integración con Paciente y Tiquete.

- NodoTiquete: nodo de la lista enlazada; guarda un Tiquete y el siguiente nodo.
- ColaTiquetes: cola FIFO dinámica (frente y fin). Permite encolar, desencolar, ver el frente, verificar si está vacía y obtener su tamaño.
- ColaPrioridad: cola de prioridad formada por varias ColaTiquetes. Atiende primero la prioridad médica más alta (Crítica, Alta, Media, Baja) a igual prioridad, los preferenciales antes que los normales y a igualdad total, por orden de llegada.
- GestorTiquetes:  Paciente, Tiquete y las colas, con una cola por tipo de atención (Consulta, Sala y Emergencias). Registra pacientes y genera su tiquete (C-001, S-001, E-001), evita tiquetes pendientes duplicados, atiende al siguiente registrando la hora y permite consultar y listar los pendientes.

#

<div align="center">

# UNIVERSIDAD FIDÉLITAS


**Curso: Estructura de Datos**

# MEDIQUEUE

### Sistema de Gestión Hospitalaria

**AVANCE 2**

---

### INTEGRANTES

| Nombre completo | Correo institucional |
|---|---|
| CASTILLO ARAGON MONSERRATH | mcastillo50644@ufide.ac.cr |
| HERNANDEZ SEGNI JOSE DANIEL | hjose90991@ufide.ac.cr |
| ALLISON EDELL ALEGRIA | aalegria50755@ufide.ac.cr |
| RAMOS RODRIGUEZ DHAYAN JESUS | dramos30655@ufide.ac.cr |

---

**Profesor:** Kenneth Artavia A.

**Costa Rica, 2026**

</div>

---

Distribución del trabajo — Avance 2
hola


