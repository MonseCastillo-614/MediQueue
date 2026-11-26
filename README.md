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
| RAMOS RODRIGUEZ DHAYAN JESUS | dramos30655@ufide.ac.cr |

---

**Profesor:** Kenneth Artavia A.

**Costa Rica, 2026**

</div>

---

Distribución del trabajo — Avance 2

Persona 1 y 2, mas la creación del Módulo 1.4: CASTILLO ARAGON MONSERRATH

Responsabilidad:
- Desarrollar la estructura para administrar salas médicas y consultorios.
- Vincularlos con la configuración de la sede.
- Manejar disponibilidad, capacidad y ocupación.
- Administrar los espacios preferenciales y la sala de emergencia crítica.
- Implementar consultas de espacios disponibles.
- Coordinar la asignación de espacios con el proceso de atención.
- Implementar el almacenamiento de las atenciones en atendidos.json.
- Implementar el almacenamiento del historial en historial.json.
- Persistencia de las salas médicas y consultorios en salas.json.
- Implementar la lectura y recuperación de los datos de salas, consultorios y atenciones al iniciar el sistema.
- Integrar la persistencia con los módulos de atención médica y administración de espacios.
- Validar el almacenamiento y la recuperación de los datos.
- Crear el árbol binario de búsqueda.
- Insertar y buscar pacientes por identificación.
- Crear el índice centralizado de pacientes.
- Implementar búsquedas de tiquetes e historiales.
- Implementar búsquedas por médico, fecha y salón/consultorio.
- Documentar los resultados de las búsquedas.

Trabajo realizado:

Durante el desarrollo del Avance 2, trabajé en la implementación e integración de los módulos correspondientes a la administración de salas médicas y consultorios, la persistencia de datos y el Módulo 1.4: Historial y búsquedas mediante árboles binarios de búsqueda (ABB).

Administración de salas médicas y consultorios

Desarrollé la estructura para administrar las salas médicas y los consultorios, vinculándolos con la configuración de la sede. Implementé el control de disponibilidad, capacidad y ocupación de los espacios, considerando también la administración de espacios preferenciales y la sala destinada a emergencias críticas.

Además, trabajé en las consultas de espacios disponibles y en la coordinación de la asignación de pacientes según el tipo de atención requerido, integrando estas funcionalidades con el proceso de atención médica.

Persistencia de datos

Implementé la persistencia de la información del sistema mediante los archivos atendidos.json, historial.json y salas.json. Esto contempla el almacenamiento de las atenciones, los historiales clínicos y la información correspondiente a las salas médicas y consultorios.

También desarrollé los procesos de lectura y recuperación de los datos al iniciar el sistema, permitiendo conservar la información registrada entre ejecuciones. Realicé pruebas para verificar el almacenamiento y la recuperación de los datos, así como la integración de la persistencia con los módulos de atención médica y administración de espacios.

Adicionalmente, implementé las funcionalidades de consulta y exportación de historiales clínicos.

Módulo 1.4: Historial y búsquedas mediante árboles ABB

Desarrollé el Módulo 1.4 mediante la implementación de un Árbol Binario de Búsqueda (ABB), utilizando la identificación de los pacientes como criterio de organización. Para ello, creé las clases NodoABB y ArbolABB, además del gestor GestorBusquedas, encargado de centralizar las consultas.

Implementé la construcción del índice de pacientes a partir de la información existente en el sistema, así como la inserción y búsqueda de pacientes por identificación. También incorporé las búsquedas de tiquetes, historiales clínicos y atenciones por médico, fecha y salón o consultorio.

Como complemento, desarrollé las operaciones del árbol estudiadas en clase, incluyendo los recorridos inorden, preorden y postorden, el conteo de nodos, el cálculo de altura, la impresión de nodos por niveles y la validación de la estructura ABB.

Finalmente, integré estas funcionalidades en el menú principal de MediQueue mediante una opción de consultas ABB y realicé pruebas funcionales para verificar la búsqueda de pacientes, la recuperación de información, los recorridos y las operaciones del árbol.

Persona 3 y 4, mas la creación del Módulo 1.5: RAMOS RODRIGUEZ DHAYAN JESUS

Responsabilidad: 

- Desarrollar las estructuras de consultas y atenciones.
- Vincular cada atención con el paciente y su tiquete.
- Registrar el médico asignado.
- Registrar diagnósticos, medicamentos, observaciones y procedimientos.
- Registrar la sala o consultorio asignado.
- Registrar el costo de la atención.
- Coordinar la integración con pacientes, tiquetes y administración de espacios.
- Desarrollar el flujo de atención y el historial clínico.
- Implementar el inicio y la finalización de las atenciones.
- Implementar la cancelación y el reingreso de pacientes.
- Desarrollar la estructura del historial mediante una pila.
- Registrar los movimientos de atención en el historial.
- Permitir consultar los últimos registros del historial.
- Coordinar la integración del proceso de atención con las colas y la asignación de salas.
- Crear hospitales, clínicas y EBAIS.
- Crear conexiones dirigidas entre sedes.
- Asignar distancia, tiempo y costo a las conexiones.
- Implementar listas de adyacencia.
- Mostrar el grafo.
- Implementar el algoritmo Dijkstra.
- Guardar y cargar los datos desde grafo.json.

Trabajo realizado:

GestorAtenciones.java
Esta clase tiene la responsabilidad de controlar el flujo de las citas médicas desde el momento en que inician hasta que finalizan por completo. Funciona como el motor principal para registrar la llegada de un paciente con su respectivo tiquete y asignarle un profesional de la salud en un espacio determinado. Durante el proceso de finalización de la consulta la clase calcula automáticamente los costos monetarios a cobrar según el tipo de servicio abarcando consultas generales, salas de atención o casos de emergencia. Un aspecto muy destacado es que se conecta a internet con las clases de OpenfdaService y mediamento para buscar información real sobre los medicamentos recetados utilizando una base de datos externa y también se encarga de filtrar la info. También permite actualizar el estado de las citas y registrar diagnósticos observaciones o procedimientos médicos aplicados. Por último se encarga de comunicarse con los archivos de persistencia para guardar y cargar toda la información de manera segura en el sistema

GestorHistorialClinico.java
Esta clase se encarga de administrar y almacenar todo el registro médico de los pacientes a lo largo del tiempo su función principal es llevar un control ordenado de cada visita que realiza una persona al centro medico para asegurar que los datos no se pierdan. Utiliza estructuras dinámicas enlazadas para agrupar el historial de todos los pacientes y apilar cada nuevo registro de forma secuencial. Permite buscar el expediente de una persona mediante su número de identificación para consultar o agregar nuevos diagnósticos a su archivo personal. Además de registrar nuevas atenciones y posibles cancelaciones el sistema facilita la lectura de los últimos movimientos clínicos de un individuo. También cuenta con la capacidad de generar un documento de texto exportable con el historial completo para guardarlo físicamente. Finalmente, el gestor valida si un paciente cumple con los requisitos necesarios para poder ingresar nuevamente a un salón de atención médica

Módulo 1.5 Gestion de Sedes con grafos
En este módulo se desarrolló un sistema de gestión de sedes médicas mediante la implementación de un grafo dirigido con listas de adyacencia. Para ello, se crearon las clases Sede, Arista, NodoSede, GrafoSedes y PersistenciaGrafo, cada una con una función específica dentro de la estructura. El sistema permite registrar hospitales, clínicas y EBAIS, así como establecer conexiones entre ellos indicando la distancia en kilómetros, el tiempo en minutos y el costo en colones. Además, permite visualizar las conexiones existentes y aplicar el algoritmo de Dijkstra para encontrar rutas óptimas según el criterio seleccionado. Finalmente, se implementó la persistencia de datos mediante un archivo JSON llamado grafo.json, con el propósito de guardar y recuperar la información. La función final de este módulo es facilitar la gestión de las sedes médicas y encontrar rutas eficientes entre ellas.
