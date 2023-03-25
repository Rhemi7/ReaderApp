package com.example.readerapp.screens.update

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.data.DataOrException
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.example.readerapp.screens.home.HomeScreenViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UpdateScreen(navController: NavController, bookId: String, viewModel: HomeScreenViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Update Book", icon = Icons.Default.ArrowBack, showProfile = false,navController = navController) {
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }) {
        val bookInfo = produceState<DataOrException<List<MBook>, Boolean, java.lang.Exception>>(initialValue = DataOrException(data = emptyList(), true, Exception("")) ) {
            value = viewModel.dataR.value
        }.value
        
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)) {
            Column(
                modifier = Modifier.padding(top = 3.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bookInfo.loading == true) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    bookInfo.loading = false

                } else {
//                    Text(text = viewModel.dataR.value.data?.get(0)?.title.toString())
                    Surface(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        elevation = 4.dp
                    ) {
                        ShowBookUpdate(bookInfo= viewModel.dataR.value, bookId= bookId)
                    }

                }
            }
        }
    }

}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, java.lang.Exception>, bookId: String) {
    Row {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
                CardListItem(book = bookInfo.data!!.first{
                    mBook -> mBook.googleBookId == bookId  
                }, onPressDetails = {})
            }
        }
    }
}

@Composable
fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
    Card(modifier = Modifier
        .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)
        .clip(
            RoundedCornerShape(20.dp)
        )
        .clickable { },
        elevation = 8.dp) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberAsyncImagePainter(model = book.photoUrl.toString()),
                contentDescription = "Image",
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp,
                            topEnd = 20.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 2.dp,
                        bottom = 0.dp)
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 8.dp)
                )
            }
        }

    }

}
