package com.example.readerapp.screens.details

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.components.RoundedButton
import com.example.readerapp.data.Resource
import com.example.readerapp.model.Item
import com.example.readerapp.navigation.ReaderScreens
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReaderBookDetailsScreen(navController: NavController,
                            bookId: String,
                            viewModel: DetailsViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Book Details", icon = Icons.Default.ArrowBack , showProfile = false, navController = navController) {
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) {
        Surface(modifier = Modifier
            .padding(3.dp)
            .fillMaxSize()) {
            Column(
                modifier = Modifier.padding(top = 30.dp, start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId)
                }.value

                if (bookInfo.data == null) {
                    LinearProgressIndicator()
                } else {
                    Surface(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .size(100.dp),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(width = 1.dp, color = Color.LightGray)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(bookInfo.data?.volumeInfo?.imageLinks?.smallThumbnail),
                            contentDescription = "Book Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(shape = CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = bookInfo.data?.volumeInfo!!.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, textAlign = TextAlign.Center
                    )
                    val bookInfo = bookInfo.data.volumeInfo
                    Text(text = "Authors: ${bookInfo.authors}")
                    Text(text = "Page Count: ${bookInfo.pageCount}")
                    Text(
                        text = "Categories: ${bookInfo.categories}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )
                    Text(
                        text = "Published: ${bookInfo.publishedDate}",
                        textAlign = TextAlign.Center
                    )
                    val cleanDesc = HtmlCompat.fromHtml(
                        bookInfo.description,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString()

                    val localDims = LocalContext.current.resources.displayMetrics
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(
                                shape = RectangleShape
                            )
                            .border(BorderStroke(width = 1.dp, color = Color.LightGray))
                            .height(localDims.heightPixels.dp.times(0.09f))
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {
                        Text(text = cleanDesc)
                    }

                    Row {
//                        Surface(
//                            Modifier
//                                .height(30.dp)
//                                .width(60.dp)
//                                .clip(
//                                    shape = RoundedCornerShape(
//                                        topStart = 10.dp,
//                                        bottomEnd = 10.dp
//                                    )
//                                ),
//                            color = Color.Cyan
//                        ) {
//                            Text(text = "Save", modifier = Modifier.align(Alignment.CenterVertically))
//                        }

                        RoundedButton(
                            "Save",
                            radius = 30
                        ) {
                            val db = FirebaseFirestore.getInstance()
//                            saveToFirebase()
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        RoundedButton(
                            "Cancel",
                            radius = 30
                        ) {
                            navController.popBackStack()
                        }
                    }

                }


            }
        }
    }

}