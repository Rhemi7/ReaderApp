@file:Suppress("NAME_SHADOWING")

package com.example.readerapp.screens.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                    val bookData = bookInfo.data.volumeInfo
                    val googleBookId = bookInfo.data.id


                    Text(text = "Authors: ${bookData.authors}")
                    Text(text = "Page Count: ${bookData.pageCount}")
                    Text(
                        text = "Categories: ${bookData.categories}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )
                    Text(
                        text = "Published: ${bookData.publishedDate}",
                        textAlign = TextAlign.Center
                    )
                    val cleanDesc = HtmlCompat.fromHtml(
                        bookData.description,
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
                            val book = MBook(
                                title = bookData.title.toString(),
                                authors = bookData.authors.toString(),
                                description = bookData.description,
                                categories = bookData.categories.toString(),
                                notes = "",
                                photoUrl = bookData.imageLinks.thumbnail,
                                publishedDate = bookData.publishedDate,
                                rating = 0.0,
                                googleBookId = googleBookId,
                                userId = FirebaseAuth.getInstance().currentUser?.uid
                            )
                            saveToFirebase(book, navController)
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

fun saveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()) {
        dbCollection.add(book).addOnSuccessListener {
            documentRef ->
            val docId = documentRef.id
            dbCollection.document(docId).update(hashMapOf("id" to docId) as Map<String, Any>).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    navController.popBackStack()
                }

            }.addOnFailureListener {
                Log.d("Doc Update Error", "saveToFirebase: Error Updating Doc")

            }
        }
    }
}
