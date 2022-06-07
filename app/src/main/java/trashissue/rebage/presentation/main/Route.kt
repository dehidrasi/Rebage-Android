package trashissue.rebage.presentation.main

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import trashissue.rebage.presentation.article.ArticleScreen
import trashissue.rebage.presentation.detection.DetectionScreen
import trashissue.rebage.presentation.favoritearticle.FavoriteArticleScreen
import trashissue.rebage.presentation.garbagebank.GarbageBankScreen
import trashissue.rebage.presentation.home.HomeScreen
import trashissue.rebage.presentation.maps.MapsScreen
import trashissue.rebage.presentation.onboarding.OnboardingScreen
import trashissue.rebage.presentation.price.PriceScreen
import trashissue.rebage.presentation.profile.ProfileScreen
import trashissue.rebage.presentation.signin.SignInScreen
import trashissue.rebage.presentation.signup.SignUpScreen
import trashissue.rebage.presentation.threers.ThreeRsScreen

sealed class Route(
    protected val route: String
) {
    object Onboarding : Route("onboarding") {

        context(NavGraphBuilder)
        fun composable() = composable(route) {
            OnboardingScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object SignIn : Route("sign_in") {

        context(NavGraphBuilder)
        fun composable() = composable(route) {
            SignInScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object SignUp : Route("sign_up") {

        context(NavGraphBuilder)
        fun composable() = composable(route) {
            SignUpScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object Home : Route("home") {

        context(NavGraphBuilder)
        fun composable() = composable(route) {
            HomeScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object Detection : Route("detection") {

        context(NavGraphBuilder)
        fun composable() = composable(route) {
            DetectionScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object Price : Route("price") {

        context(NavGraphBuilder)
        fun composable() = composable(route) {
            PriceScreen()
        }

        operator fun invoke() = route
    }

    object Profile : Route("profile") {

        context(NavGraphBuilder)
        fun composable() = composable(route) {
            ProfileScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object FavoriteArticle : Route("favorite_article") {

        context (NavGraphBuilder)
        fun composable() = composable(route) {
            FavoriteArticleScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object ThreeRs : Route("three_r_s/{$KEY_DETECTION_ID}") {
        private val arguments = listOf(
            navArgument(KEY_DETECTION_ID) {
                type = NavType.IntType
            }
        )

        context (NavGraphBuilder)
        fun composable() = composable(route, arguments) {
            ThreeRsScreen(LocalNavController.current)
        }

        operator fun invoke(id: Int) = "three_r_s/$id"
    }

    object Article : Route("article/{$KEY_ARTICLE_ID}") {
        private val arguments = listOf(
            navArgument(KEY_ARTICLE_ID) {
                type = NavType.IntType
            }
        )

        context (NavGraphBuilder)
        fun composable() = composable(route, arguments) {
            ArticleScreen(LocalNavController.current)
        }

        operator fun invoke(articleId: Int) = "article/$articleId"
    }

    object GarbageBank : Route("garbage_bank") {

        context (NavGraphBuilder)
        fun composable() = composable(route) {
            GarbageBankScreen(LocalNavController.current)
        }

        operator fun invoke() = route
    }

    object Maps : Route("maps/{$KEY_LAT}/{$KEY_LNG}") {
        private val arguments = listOf(
            navArgument(KEY_LAT) {
                type = NavType.StringType
            }
        )

        context (NavGraphBuilder)
        fun composable() = composable(route, arguments) {
            val latLng = remember {
                val lat = it.arguments?.getString(KEY_LAT)?.toDoubleOrNull()
                val lng = it.arguments?.getString(KEY_LNG)?.toDoubleOrNull()
                lat to lng
            }
            val (lat, lng) = latLng

            if (lat != null && lng != null) {
                MapsScreen(LocalNavController.current, lat, lng)
            }
        }

        operator fun invoke(lat: Double, lng: Double) = "maps/$lat/$lng"
    }

    companion object {
        const val KEY_ARTICLE_ID = "articleId"
        const val KEY_DETECTION_ID = "detectionId"
        const val KEY_LAT = "lat"
        const val KEY_LNG = "lng"
    }
}
