package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.database.cache.Database
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)

//sample code used from: https://developer.android.com/develop/ui/compose/components/drawer
@Composable
fun MenuHeader(
    content: @Composable (PaddingValues) -> Unit,
    navController: NavController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet (
                drawerContainerColor = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(50.dp))

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Create new sample",
                                style = TextStyle(fontFamily = KhandFontFamily(), fontSize = 30.sp,
                                    color = Color.Black)
                            )
                        },
                        selected = false,
                        onClick = { navController.navigate("selectCollection") }
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Create new form",
                                style = TextStyle(fontFamily = KhandFontFamily(), fontSize = 30.sp,
                                    color = Color.Black)


                            )
                        },
                        selected = false,
                        onClick = { navController.navigate("newForm") }
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "View all samples",
                                style = TextStyle(fontFamily = KhandFontFamily(), fontSize = 30.sp,
                                    color = Color.Black)
                            )
                        },
                        selected = false,
                        onClick = { navController.navigate("viewSampleCollection") }
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "View all forms",
                                style = TextStyle(fontFamily = KhandFontFamily(), fontSize = 30.sp,
                                    color = Color.Black)
                            )
                        },
                        selected = false,
                        onClick = { /* add form screen */ },
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Q/A",
                                style = TextStyle(fontFamily = KhandFontFamily(), fontSize = 30.sp,
                                    color = Color.Black)
                            )
                        },
                        selected = false,
                        onClick = { navController.navigate("QandA_screen") },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = "Gator Field Notebook",
                                style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                                color = Color.White,
                                fontSize = 30.sp,
                                modifier = Modifier.padding(horizontal = 40.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0021A5)
                    )
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}