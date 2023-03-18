package com.example.composetest

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { Column(Modifier.fillMaxSize().background(Color.Gray)) { animateDecayDemo() } }
  }

  // Column Row Box 布局
  @Composable
  private fun GroupViewTest() {
    Column(Modifier.size(200.dp, 400.dp).background(Color.Gray)) {
      Text(text = "测试", Modifier.background(Color.Green).padding(50.dp))
      Text(text = "测试", Modifier.background(Color.Red).padding(40.dp))
      Text(text = "测试", Modifier.background(Color.Blue).padding(30.dp))
      Image(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = null,
      )
    }
  }

  // lazyRow LazyColumn
  @Composable
  private fun ListTest() {
    val titles = listOf("探探", "陌陌", "soul")
    Box(Modifier.fillMaxSize().background(Color.Gray)) {
      LazyColumn {
        item {
          Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
          )
        }
        items(titles) { Text(text = it, Modifier.background(Color.Green).padding(30.dp)) }
      }
    }
  }

  // Modifier的顺序性：Modifier 没有Margin
  // 通用属性用Modifier，如宽高等；专用属性用函数参数，如字体颜色等
  @Composable
  private fun ModifierTest() {
    Column(Modifier.fillMaxSize().background(Color.Gray)) {
      Text(text = "测试", Modifier.background(Color.Green).size(150.dp).padding(50.dp))
      //      Text(text = "测试", Modifier.padding(50.dp).background(Color.Green).size(150.dp))
    }
  }

  // 伴生对象 扩展函数 then CombinedModifier
  // 常量的修改实时显示
  @Composable
  private fun ModifierDetailTest() {
    Column(Modifier.fillMaxSize().background(Color.Gray)) {
      Text(text = "测试", Modifier.background(Color.Green).size(150.dp).padding(50.dp))
    }
  }

  // 传统View嵌套Compose：1. 在XML中引入ComposeView 2. 在代码中向ComposeView 注入 composeView
  // Compose嵌套原生传统View
  @Composable
  @Preview
  private fun AndroidViewTest() {
    val context = LocalContext.current
    var name by remember { mutableStateOf("探探") }
    Column(Modifier.fillMaxSize().background(Color.Gray)) {
      AndroidView(factory = { TextView(context).apply { text = "测试" } }) { it.text = name }
      Text(text = "按钮", Modifier.clickable { name += "~" })
    }
  }

  // -----------------------------------------------------------------------------------------------

  @Composable
  private fun AndroidView() {
    var isBlack by remember() { mutableStateOf(true) }
    //        val isString by remember() { mutableStateOf("BUTTON") }
    //        val isString02 by remember() { derivedStateOf { isString } }
    Column(Modifier.fillMaxSize().background(Color.Gray)) {
      Text(text = "测试")
      Box(Modifier.size(200.dp).background(if (isBlack) Color.Red else Color.Green)) {
        Button(onClick = { isBlack = !isBlack }) { Text(text = "按钮") }
      }
    }
  }

  @Composable
  private fun animateXXXAsStateDemo() {
    var isChangeSize by remember { mutableStateOf(false) }
    val size by animateDpAsState(if (isChangeSize) 200.dp else 100.dp, TweenSpec())
    Column(Modifier.fillMaxSize().background(Color.Gray)) {
      Box(Modifier.size(size).background(Color.Green).padding(10.dp)) {
        Button(onClick = { isChangeSize = !isChangeSize }) { Text(text = "按钮") }
      }
    }
  }

  @Composable
  private fun animatableDemo() {
    var isChangeSize by remember { mutableStateOf(false) }
    val anim = remember { Animatable(100.dp, Dp.VectorConverter) }
    LaunchedEffect(isChangeSize) {
      anim.snapTo(if (isChangeSize) 400.dp else 0.dp)
      anim.animateTo(if (isChangeSize) 200.dp else 100.dp)
      //            anim.updateBounds(50.dp, 400.dp)
    }
    Column(Modifier.fillMaxSize().background(Color.Gray)) {
      Box(Modifier.size(anim.value).background(Color.Green).padding(10.dp)) {
        Button(onClick = { isChangeSize = !isChangeSize }) { Text(text = "按钮") }
      }
    }
  }

  @Composable
  private fun animateDecayDemo() {
    val anim = remember { Animatable(0.dp, Dp.VectorConverter) }
    val decay = rememberSplineBasedDecay<Dp>()
    //        val decay = exponentialDecay<Dp>(0.5f)
    LaunchedEffect(Unit) {
      delay(1000)
      anim.animateDecay(2000.dp, decay) { println(value) }
    }
    Column(Modifier.fillMaxSize().background(Color.Gray).padding(0.dp, anim.value, 0.dp, 0.dp)) {
      Box(Modifier.size(100.dp).background(Color.Green).padding(10.dp))
    }
  }

  @Composable
  private fun transitionDemo() {
    var isShow by remember { mutableStateOf(false) }
    val transition = updateTransition(isShow, "isShow") // updateTransition 创建+更新
    val paddingStart by
      transition.animateDp(
        label = "paddingLeft",
        transitionSpec = { if (!initialState && targetState) tween() else spring() }
      ) {
        if (it) 50.dp else 0.dp
      }
    val size by
      transition.animateDp(
        label = "round",
        transitionSpec = { if (false isTransitioningTo true) tween() else spring() }
      ) {
        if (it) 300.dp else 100.dp
      }
    Column(Modifier.fillMaxSize().background(Color.Gray)) {
      Box(
        Modifier.size(size)
          .background(Color.Green)
          .padding(paddingStart, 0.dp, 0.dp, 0.dp)
          .padding(10.dp)
      ) {
        Button(onClick = { isShow = !isShow }) { Text(text = "按钮") }
      }
    }
  }

  @Composable
  private fun animateVisibltyDemo() {
    var isChangeSize by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Color.Gray).padding(10.dp, 10.dp, 10.dp, 10.dp)) {
      Button(onClick = { isChangeSize = !isChangeSize }) { Text(text = "按钮") }
      Crossfade(isChangeSize) {
        if (it) {
          Box(Modifier.size(100.dp).background(Color.Green).padding(10.dp))
        }
      }
      AnimatedVisibility(visible = isChangeSize, enter = fadeIn() + expandHorizontally()) {
        Box(Modifier.size(100.dp).padding(10.dp).background(Color.Green))
      }
    }
  }

  @OptIn(ExperimentalAnimationApi::class)
  @Composable
  private fun MyUi() {
    BoxWithConstraints {
      var isShow by remember { mutableStateOf(true) }
      Column(Modifier.fillMaxSize().padding(0.dp, 0.dp, 0.dp, 0.dp)) {
        AnimatedContent(
          isShow,
          transitionSpec = {
            (scaleIn(initialScale = 0.0f, animationSpec = tween(220, delayMillis = 90)) with
              scaleOut(targetScale = 0.1f, animationSpec = tween(220, delayMillis = 90)))
          }
        ) {
          if (it)
            Box(
              Modifier.size(90.dp).clip(RoundedCornerShape(20.dp)).background(Color.Red).clickable {
                isShow = !isShow
              }
            ) {}
          else
            Box(
              Modifier.size(90.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Gray)
                .clickable { isShow = !isShow }
            ) {}
        }
      }
    }
  }
}
