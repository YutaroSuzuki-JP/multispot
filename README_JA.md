# Multispot

![Multispot Hero](assets/hero.jpg)

[English README is here](README.md)

**Multispot** は、**Kotlin Multiplatform (KMP)** および **Compose Multiplatform** 用に設計された、軽量で美しく、自由度の高いコーチマーク（機能ハイライト・チュートリアル）ライブラリです。

暗幕オーバーレイ、多様なくり抜き形状、ダイナミックな吹き出しポップアップを用いて、アプリ内の特定コンポーネントを簡単にハイライト表示できます。レイアウトの階層がどれだけ深くても（深いカラム、行、カード、スクロールリスト等）、自動で正確な座標をトラッキングします。

---

## 主な特徴

- 🌍 **マルチプラットフォーム**: Android、iOS、および JVM Desktop で完全に同じUI描画・レイアウトロジックを共有します。
- 🎯 **ディープ階層トラッキング**: ターゲットがどんなに深い階層にあっても、親の領域からの相対座標を自動計算。ウィンドウのリサイズや画面構成の変化にも追従します。
- 🔮 **自由なくり抜き形状**: スポットライトのくり抜き方をカスタム可能：
  - `SpotShape.Circle` / `SpotShape.Rect` / `SpotShape.RoundedRect` (角丸長方形)
  - `SpotShape` インターフェースを実装することで、利用側で「ひし形」などの完全にカスタムな切り抜き形状を作ることも可能です。
- 🎨 **豊富な吹き出しスタイル (`TooltipStyle`)**:
  - `Arrow`: 吹き出しの枠線を無くし、手書き風の美しい曲線矢印でターゲットを指し示します。
  - `Balloon` / `Glass` (半透明グラス風) / `Outline` (枠線のみ): ポインタ（ツノ）がターゲットの方向を自動的に追従して指し示す、チャット風吹き出しを表示します。
  - `Custom`: ライブラリ側のコンテナラッピングをバイパスし、利用側で枠線や影、角丸などを完全にカスタムしたオリジナルの吹き出しをレイアウトできます。
- 🛡️ **重なり防止とスマート余白調整**:
  - ターゲットの周りの空きスペースを自動測定し、上下左右の最も広いスペースへ吹き出しを退避させ、重なりを防止します。
  - 吹き出しのスタイルに応じて、ポインタとターゲット間の距離を自動でスマートに調整します。

---

## 導入方法

共有する Kotlin Multiplatform モジュールの `build.gradle.kts` にライブラリを追加します：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.multispot:multispot:1.0.0") // 実際のバージョンに置き換えてください
        }
    }
}
```

---

## 基本的な使い方

### 1. State と Area コンテナの配置
画面全体を `MultispotArea` で囲み、`MultispotState` を定義して progression を管理します。

```kotlin
import io.github.yutarosuzuki_jp.multispot.*

@Composable
fun MyScreen() {
    val state = rememberMultispotState()

    MultispotArea(
        state = state,
        overlayColor = Color.Black.copy(alpha = 0.75f)
    ) {
        MainContent(state = state)
    }
}
```

### 2. ターゲットの登録
チュートリアルでハイライトしたいコンポーネントに `Modifier.multispot(...)` を付与し、ステップ順序や表示したい吹き出し Composable を定義します。

```kotlin
@Composable
fun MainContent(state: MultispotState) {
    Column {
        Text(
            text = "Welcome to Multispot!",
            modifier = Modifier.multispot(
                state = state,
                step = 0,
                key = "welcome_title",
                shape = SpotShape.RoundedRect(8.dp),
                tooltipStyle = TooltipStyle.Balloon,
                tooltip = {
                    TooltipBalloon(
                        title = "Welcome Tutorial",
                        message = "This header title is highlighted as step 0.",
                        style = TooltipStyle.Balloon
                    )
                }
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .multispot(
                    state = state,
                    step = 1,
                    key = "action_btn",
                    shape = SpotShape.Circle(radius = 24.dp, margin = 8.dp),
                    tooltipStyle = TooltipStyle.Arrow,
                    onTargetClicked = {
                        state.next()
                    },
                    tooltip = {
                        TooltipBalloon(
                            title = "Settings Action",
                            message = "Tap this settings button highlight to trigger custom action and go next.",
                            style = TooltipStyle.Arrow
                        )
                    }
                )
        ) {
            Button(onClick = { state.start() }) {
                Text("Start Walkthrough")
            }
        }
    }
}
```

---

## ライセンス

MIT ライセンスに基づいて公開されています。詳細は LICENSE ファイルを参照してください。
