package com.example.readerapp.screens.update

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.readerapp.R
import com.example.readerapp.components.InputField
import com.example.readerapp.components.RatingBar
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.components.RoundedButton
import com.example.readerapp.data.DataOrException
import com.example.readerapp.data.Resource
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.example.readerapp.screens.home.HomeScreenViewModel
import com.example.readerapp.utils.formatDate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UpdateScreen(navController: NavController, bookId: String, viewModel: HomeScreenViewModel = hiltViewModel()) {
    val bookInfo = produceState<DataOrException<List<MBook>, Boolean, java.lang.Exception>>(
        initialValue = DataOrException(
            data = emptyList(),
            true,
            Exception("")
        )
    ) {
        value = viewModel.dataR.value
    }.value

    Scaffold(topBar = {
        ReaderAppBar(title = "Update Book", icon = Icons.Default.ArrowBack, showProfile = false,navController = navController) {
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }) {

        
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
                    ShowSimpleForm(book= viewModel.dataR.value.data?.first { mBook ->
                        mBook.googleBookId == bookId
                    }!!, navController)



                }
            }
        }
    }

}

@Composable
fun ShowSimpleForm(book: MBook, navController: NavController) {
    val notesText = remember {
        mutableStateOf("")
    }

    val isStartedReading = remember {
        mutableStateOf(false)
    }
    val isFinishedReading = remember {
        mutableStateOf(false)
    }

    val ratingVal = remember {
        mutableStateOf(0)
    }

    val context = LocalContext.current

    SimpleForm(
        defaultValue = book.notes.toString().ifEmpty { "No thoughts available" },
    ) {
        note -> notesText.value = note
    }

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(onClick = { isStartedReading.value = true}, enabled = book.startedReading == null) {
            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Start Reading")
                } else {
                    Text(
                        text = "Started Reading!",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(0.5f)
                    )
                }
            } else {
                Text(text = "Started on ${formatDate(book.startedReading!!)}")
            }

        }

        Spacer(modifier = Modifier.width(4.dp))

        TextButton(onClick = { isFinishedReading.value = true}, enabled = book.finishedReading == null) {
            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark as Read")
                } else {
                    Text(
                        text = "Finished Reading!",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(0.5f)
                    )
                }
            } else {
                Text(text = "Finished on ${formatDate(book.finishedReading!!)}")
            }

        }
    }

    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp))
    book.rating?.toInt().let {
        RatingBar(rating = it!!) {rating ->
            ratingVal.value = rating
        }
    }
    Spacer(modifier = Modifier.height(20.dp))

   Row(
       modifier = Modifier.fillMaxWidth(),
       horizontalArrangement = Arrangement.SpaceAround
   ) {
       val changedNotes = book.notes != notesText.value
       val changedRating = book.rating?.toInt() != ratingVal.value
       val isFinishedTimeStamp = if (isFinishedReading.value ) com.google.firebase.Timestamp.now() else book.finishedReading
       val isStartedTimestamp = if (isStartedReading.value) com.google.firebase.Timestamp.now() else book.startedReading
//           navController.popBackStack()

       val bookUpdate = changedNotes || changedRating || isStartedReading.value || isFinishedReading.value
       val bookToUpdate = hashMapOf(
           "finished_reading_at" to isFinishedTimeStamp,
           "started_reading_at" to isStartedTimestamp,
           "rating" to ratingVal.value,
           "notes" to notesText.value
       ).toMap()

       RoundedButton(
           "Update",
           radius = 30
       ) {
          if (bookUpdate) {
              FirebaseFirestore.getInstance().collection("books").document(book.id!!)
                  .update(bookToUpdate).addOnCompleteListener {
                      showToast(context, "Book Updated Successfully")
                      navController.navigate(ReaderScreens.ReaderHomeScreen.name)
              }.addOnFailureListener {
                      Log.w("Error", "Error updating document" , it)

              }

          }
       }
       val openDialog = remember {
           mutableStateOf(false)
       }

       if (openDialog.value) {
           ShowAlertDialog(message = stringResource(id = R.string.sure) + "\n" + stringResource(id = R.string.action), openDialog) {
               FirebaseFirestore.getInstance().collection("books").document(book.id!!).delete().addOnCompleteListener {
                   if (it.isSuccessful) {
                       openDialog.value = false
//                       navController.popBackStack()
                       navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                   }
               }
           }
       }
       RoundedButton(
           "Delete",
           radius = 30
       ) {
            openDialog.value = true
       }
   }
}

@Composable
fun ShowAlertDialog(message: String, openDialog: MutableState<Boolean>, onYesPressed: () -> Unit) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = { openDialog.value = false }, title = { Text(text = "Delete Book")}, text = { Text(text = message)},
            buttons = { Row(
            modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { onYesPressed.invoke() }) {
                Text(text = "Yes")
            }

            TextButton(onClick = { openDialog.value = false }) {
                Text(text = "No")
            }
        }}) 
    }

}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book",
    onSearch: (String) -> Unit
) {
    Column {
        val  textFieldValue = rememberSaveable {
            mutableStateOf(defaultValue)
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) {
            textFieldValue.value.trim().isNotEmpty()
        }

        InputField(
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
                    valueState = textFieldValue,
            labelId = "Enter your Thoughts",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            })
    }

}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, java.lang.Exception>, bookId: String) {
    Row {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
//            Log.d("MBook ID", "ShowBookUpdate: ${bookInfo.data!!.firstOrNull()?.googleBookId}")
//            Log.d("Book ID", "ShowBookUpdate: ${bookId}")

            val book = bookInfo.data!!.firstOrNull{ mBook ->
               mBook.googleBookId.toString() == bookId}
            Log.d("Data displayed", "ShowBookUpdate: ${book!!.googleBookId}")
            Log.d("Book ID", "ShowBookUpdate: ${bookId}")

            Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
                CardListItem(book = book!!, onPressDetails = {})
            }
        } else {
            Text(text = "No Data")
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
