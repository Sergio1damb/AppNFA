package com.example.appnfa

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.Calendar

data class Jugador(
    val nombre: String,
    val equipo: String,
    val posiciones: List<String>,
    val puntos: Int,
    val equipoContrario: String,
    val fechaPartido: String
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val jugadores = mutableStateOf(listOf(
                Jugador("LeBron James", "Los Angeles Lakers", listOf("SF", "PF"), 25, "Golden State Warriors", "01/12/2023"),
                Jugador("Stephen Curry", "Golden State Warriors", listOf("PG"), 30, "Los Angeles Lakers", "01/12/2023"),
                Jugador("Kevin Durant", "Brooklyn Nets", listOf("SF", "PF"), 28, "New York Knicks", "02/12/2023"),
                Jugador("Giannis Antetokounmpo", "Milwaukee Bucks", listOf("PF", "C"), 32, "Chicago Bulls", "02/12/2023"),
                Jugador("Kawhi Leonard", "Los Angeles Clippers", listOf("SF", "PF"), 27, "Phoenix Suns", "03/12/2023"),
                Jugador("Luka Dončić", "Dallas Mavericks", listOf("PG", "SG"), 29, "Houston Rockets", "03/12/2023"),
                Jugador("Joel Embiid", "Philadelphia 76ers", listOf("C"), 30, "Boston Celtics", "04/12/2023"),
                Jugador("Nikola Jokić", "Denver Nuggets", listOf("C"), 26, "Portland Trail Blazers", "04/12/2023"),
                Jugador("James Harden", "Brooklyn Nets", listOf("SG", "PG"), 24, "New York Knicks", "05/12/2023"),
                Jugador("Anthony Davis", "Los Angeles Lakers", listOf("PF", "C"), 28, "Golden State Warriors", "05/12/2023")
            ))
            val equipos = listOf(
                "Atlanta Hawks",
                "Boston Celtics",
                "Brooklyn Nets",
                "Charlotte Hornets",
                "Chicago Bulls",
                "Cleveland Cavaliers",
                "Dallas Mavericks",
                "Denver Nuggets",
                "Detroit Pistons",
                "Golden State Warriors",
                "Houston Rockets",
                "Indiana Pacers",
                "Los Angeles Clippers",
                "Los Angeles Lakers",
                "Memphis Grizzlies",
                "Miami Heat",
                "Milwaukee Bucks",
                "Minnesota Timberwolves",
                "New Orleans Pelicans",
                "New York Knicks",
                "Oklahoma City Thunder",
                "Orlando Magic",
                "Philadelphia 76ers",
                "Phoenix Suns",
                "Portland Trail Blazers",
                "Sacramento Kings",
                "San Antonio Spurs",
                "Toronto Raptors",
                "Utah Jazz",
                "Washington Wizards"
            )

            val navController = rememberNavController()
            NavHost(navController, startDestination = "main") {
                composable("main") {
                    PantallaPrincipal(navController, jugadores, equipos)
                }
                composable("crear") {
                    PantallaCrearJugador(navController, jugadores, equipos)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(navController: NavController, jugadores: MutableState<List<Jugador>>, equipos: List<String>) {
    var textoBusqueda by remember { mutableStateOf("") }
    var modoBorrado by remember { mutableStateOf(false) }
    var jugadoresSeleccionados by remember { mutableStateOf(List(jugadores.value.size) { false }) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(Color.Blue),
                title = { Text(text = "Bienvenido Usuario", color = Color.White) },
            )
        }
    ) { paddingValues ->
        Column {
            TextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Busca a jugadores por equipo") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(contentPadding = paddingValues) {
                itemsIndexed(jugadores.value) { index, jugador ->
                    JugadorCard(jugador = jugador, seleccionado = jugadoresSeleccionados[index], modoBorrado = modoBorrado
                    ) {
                        jugadoresSeleccionados = jugadoresSeleccionados.toMutableList().apply { this[index] = it }
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
            ExtendedFloatingActionButton(
                text = { Text("Añadir") },
                onClick = { if (!modoBorrado) navController.navigate("crear") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                modifier = Modifier.padding(16.dp)
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            ExtendedFloatingActionButton(
                text = { Text(if (modoBorrado) "Confirmar borrado" else "Eliminar") },
                onClick = {
                    if (modoBorrado) {
                        jugadores.value = jugadores.value.filterIndexed { index, _ -> !jugadoresSeleccionados[index] }
                        jugadoresSeleccionados = List(jugadores.value.size) { false }
                    }
                    modoBorrado = !modoBorrado
                },
                icon = { Icon(if (modoBorrado) Icons.Filled.Check else Icons.Filled.Delete, contentDescription = null) },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun JugadorCard(jugador: Jugador, seleccionado: Boolean, modoBorrado: Boolean, onSeleccionadoChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(enabled = modoBorrado) { onSeleccionadoChange(!seleccionado) },
        colors = CardDefaults.cardColors(cambiarColorEquipo(jugador.equipo))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (modoBorrado) {
                Checkbox(checked = seleccionado, onCheckedChange = onSeleccionadoChange)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Image(
                painter = painterResource(id = R.drawable.bertram),
                contentDescription = "Imagen del jugador",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = jugador.nombre, fontWeight = FontWeight.Bold)
                Text(text = "VS ${jugador.equipoContrario}, ${jugador.fechaPartido}")
                Text(text = "${jugador.puntos} PP")
            }
        }
    }
}

@Composable
fun cambiarColorEquipo(team: String): Color {
    return when (team) {
        "Los Angeles Lakers" -> Color.Yellow
        "Golden State Warriors" -> Color.Blue
        "Chicago Bulls" -> Color.Red
        "Boston Celtics" -> Color.Green
        "Atlanta Hawks" -> Color.Red
        "Brooklyn Nets" -> Color.Magenta
        "Charlotte Hornets" -> Color.Cyan
        "Cleveland Cavaliers" -> Color.Magenta
        "Dallas Mavericks" -> Color.Blue
        "Denver Nuggets" -> Color.Blue
        "Detroit Pistons" -> Color.Blue
        "Houston Rockets" -> Color.Red
        "Indiana Pacers" -> Color.Yellow
        "Los Angeles Clippers" -> Color.Red
        "Memphis Grizzlies" -> Color.Blue
        "Miami Heat" -> Color.Red
        "Milwaukee Bucks" -> Color.Green
        "Minnesota Timberwolves" -> Color.Blue
        "New Orleans Pelicans" -> Color.Blue
        "New York Knicks" -> Color.Blue
        "Oklahoma City Thunder" -> Color.Blue
        "Orlando Magic" -> Color.Blue
        "Philadelphia 76ers" -> Color.Blue
        "Phoenix Suns" -> Color.Cyan
        "Portland Trail Blazers" -> Color.Red
        "Sacramento Kings" -> Color.Cyan
        "San Antonio Spurs" -> Color.Gray
        "Toronto Raptors" -> Color.Red
        "Utah Jazz" -> Color.Green
        "Washington Wizards" -> Color.Red
        else -> Color.Gray
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearJugador(navController: NavController, jugadores: MutableState<List<Jugador>>, equipos: List<String>) {
    var nombre by remember { mutableStateOf(jugadores.value.first().nombre) }
    var equipo by remember { mutableStateOf(jugadores.value.first().equipo) }
    var posiciones by remember { mutableStateOf(listOf<String>()) }
    var puntos by remember { mutableStateOf(0) }
    var equipoContrario by remember { mutableStateOf(equipos.first()) }
    var fechaPartido by remember { mutableStateOf("") }
    var isDatePickerDialogOpen by remember { mutableStateOf(false) }
    var isEquipoDropdownOpen by remember { mutableStateOf(false) }

    if (isDatePickerDialogOpen) {
        Dialog(onDismissRequest = { isDatePickerDialogOpen = false }) {
            DatePicker(value = fechaPartido, onValueChange = { fechaPartido = it })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Introduce el nombre del jugador") },
            placeholder = {
                Text(text = "Selecciona un valor")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = equipo,
            onValueChange = { equipo = it },
            label = { Text("El equipo en el que juega") },
            placeholder = {
                Text(text ="Selecciona un valor")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Selecciona una o más posiciones:")
        var posiciones by remember { mutableStateOf(mutableListOf<String>()) }
        var options by remember { mutableStateOf(listOf("PF", "C", "PG", "SG", "SF")) }

        CheckboxGroup(options = options, selectedOptions = posiciones)

        Spacer(modifier = Modifier.height(16.dp))
        Row{
            Text("El número de puntos que metió en el partido:")
            Slider(
                value=puntos.toFloat(),
                onValueChange={puntos=it.toInt()},
                valueRange=0f..100f,
                modifier=Modifier.weight(1f)
            )
            Text(puntos.toString())
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value=equipoContrario,
            onValueChange={equipoContrario=it},
            label={Text("El equipo contra el que se jugó")},
            placeholder={
                Text(text="Selecciona un valor")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Selecciona la fecha del partido:",
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { isDatePickerDialogOpen = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
            }
        }
        Text(
            text = fechaPartido,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                jugadores.value = jugadores.value + Jugador(nombre, equipo, posiciones, puntos, equipoContrario, fechaPartido)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear jugador")
        }
    }
}


@Composable
fun PantallaDetalleJugador(jugador: Jugador) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(text = "Nombre: jugador.nombre")
        Text(text="Equipo: ${jugador.equipo}")
        Text(text = "Puntos en el partido seleccionado: ${jugador.puntos}")
    }
}

@Composable
fun CheckboxGroup(
    options: List<String>,
    selectedOptions: MutableList<String>
) {
    options.forEach { item ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            var isChecked by remember { mutableStateOf(selectedOptions.contains(item)) }
            Checkbox(
                checked = isChecked,
                onCheckedChange = { selected ->
                    isChecked = selected
                    if (selected) {
                        selectedOptions.add(item)
                    } else {
                        selectedOptions.remove(item)
                    }
                }
            )
            Text(text = item, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun DatePicker(
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    LaunchedEffect(key1 = true) {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                onValueChange("$selectedYear/${selectedMonth + 1}/$selectedDayOfMonth")
            },
            year,
            month,
            dayOfMonth
        ).show()
    }
}
