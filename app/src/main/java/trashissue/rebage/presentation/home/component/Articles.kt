package trashissue.rebage.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import trashissue.rebage.R
import trashissue.rebage.domain.model.Article
import trashissue.rebage.domain.model.Result
import trashissue.rebage.domain.model.empty
import trashissue.rebage.domain.model.success
import trashissue.rebage.presentation.theme3.RebageTheme3

private val ContentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

@Composable
fun Articles(
    modifier: Modifier = Modifier,
    label: String,
    articles: Result<List<Article>>,
    onClickArticle: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1F),
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(R.string.text_see_more),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = ContentPadding
        ) {
            articles.empty {
                item {
                    // TODO: Please create shimmer animation
                    Article(
                        modifier = Modifier
                            .width(240.dp)
                            .alpha(0F),
                        id = 0,
                        title = " ",
                        photo = null
                    )
                }
            }
            articles.success { articles ->
                items(items = articles, key = { it.id }) { article ->
                    Article(
                        modifier = Modifier.width(240.dp),
                        id = article.id,
                        title = article.title,
                        photo = article.photo.getOrNull(0),
                        onClick = onClickArticle
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticlesPreview() {
    RebageTheme3 {
        Articles(
            label = stringResource(R.string.text_all_articles),
            articles = Result.Success(
                (0..10).map {
                    Article(
                        id = 1,
                        title = "title $it",
                        body = "",
                        createdAt = "",
                        updatedAt = "",
                        source = "",
                        photo = listOf("https://i.pinimg.com/564x/d7/f8/5e/d7f85e8343547676774a4ffdffc96143.jpg"),
                        author = "",
                    )
                }),
            onClickArticle = { }
        )
    }
}
