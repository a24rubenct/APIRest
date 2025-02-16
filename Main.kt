import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Int,
    val tags: List<String>,
    val userId: Int,
    val image: String,
    val rating: Double,
    val reviewCount: Int,
    val mealType: List<String>
)

@Serializable
data class RecipesResponse(
    val recipes: List<Recipe>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

fun main() {

    //  crear cliente http
    val client = HttpClient.newHttpClient()

    // crear solicitud
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://dummyjson.com/recipes"))
        .GET()
        .build()

    //  Enviar la solicitud con el cliente
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    // obtener string con datos
    val jsonBody = response.body()

    // Deserializar el JSON a una lista de objetos Recipe
    // hay que hacerlo en dos pasos ya que la respuesta de esta API así lo requiere
    val recipesResponse: RecipesResponse = Json.decodeFromString(jsonBody)
    val recipes: List<Recipe> = recipesResponse.recipes

    // Algunos datos de las recetas fáciles ("Easy") de cena ("Dinner") ordenadas por tiempo total a emplear de menos a más
    val easyDinnerRecipes = recipes.filter { it.difficulty.equals("Easy") && it.mealType.contains("Dinner") }
    val sortedEasyDinnerRecipes = easyDinnerRecipes.sortedBy { (it.prepTimeMinutes + it.cookTimeMinutes) }
    sortedEasyDinnerRecipes.forEach { recipe ->
        println("Nombre: ${recipe.name}, Valoración: ${recipe.rating}, Tiempo: ${recipe.prepTimeMinutes + recipe.cookTimeMinutes}")
    }

    // Algunos datos de las recetas de almuerzo ("Lunch") asiático con menos de 300kcal y ordenadas por valoración de mayor a menor
    val lunchFitRecipes = recipes.filter { it.mealType.contains("Lunch") && it.cuisine.contains("Asian") && it.caloriesPerServing < 300 }
    val sortedLunchFitRecipes = lunchFitRecipes.sortedByDescending { it.rating }
    sortedLunchFitRecipes.forEach { recipe ->
        println("Nombre: ${recipe.name}, Calorías: ${recipe.caloriesPerServing}, Valoración: ${recipe.rating}")
    }

    // Algunos datos de las recetas sin horno ("oven") de nivel medio ordenadas por calorías de menos a más
    val noOvenMidRecipes = recipes.filter { !it.instructions.contains("oven") && it.difficulty.equals("Medium") }
    val sortedNoOvenMidRecipes = noOvenMidRecipes.sortedBy { it.caloriesPerServing }
    sortedNoOvenMidRecipes.forEach { recipe ->
        println("Nombre: ${recipe.name}, Tipo: ${recipe.mealType}, Valoración: ${recipe.rating}, Calorías: ${recipe.caloriesPerServing}")
    }
}



