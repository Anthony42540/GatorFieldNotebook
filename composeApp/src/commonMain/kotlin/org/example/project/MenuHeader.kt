package org.example.project

import KhandFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


//@Composable
//fun DetailedDrawerExample(
//    content: @Composable (PaddingValues) -> Unit
//) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//
//    ModalNavigationDrawer(
//        drawerContent = {
//            ModalDrawerSheet {
//                Column(
//                    modifier = Modifier.padding(horizontal = 16.dp)
//                        .verticalScroll(rememberScrollState())
//                ) {
//                    Spacer(Modifier.height(12.dp))
//                    Text("Drawer Title", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
//                    HorizontalDivider()
//
//                    Text("Section 1", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
//                    NavigationDrawerItem(
//                        label = { Text("Item 1") },
//                        selected = false,
//                        onClick = { /* Handle click */ }
//                    )
//                    NavigationDrawerItem(
//                        label = { Text("Item 2") },
//                        selected = false,
//                        onClick = { /* Handle click */ }
//                    )
//
//                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
//
//                    Text("Section 2", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
//                    NavigationDrawerItem(
//                        label = { Text("Settings") },
//                        selected = false,
//                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
//                        badge = { Text("20") }, // Placeholder
//                        onClick = { /* Handle click */ }
//                    )
//                    NavigationDrawerItem(
//                        label = { Text("Help and feedback") },
//                        selected = false,
//                        icon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
//                        onClick = { /* Handle click */ },
//                    )
//                    Spacer(Modifier.height(12.dp))
//                }
//            }
//        },
//        drawerState = drawerState
//    ) {
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Navigation Drawer Example") },
//                    navigationIcon = {
//                        IconButton(onClick = {
//                            scope.launch {
//                                if (drawerState.isClosed) {
//                                    drawerState.open()
//                                } else {
//                                    drawerState.close()
//                                }
//                            }
//                        }) {
//                            Icon(Icons.Default.Menu, contentDescription = "Menu")
//                        }
//                    }
//                )
//            }
//        ) { innerPadding ->
//            content(innerPadding)
//        }
//    }
//}