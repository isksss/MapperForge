# 0088 Lowercase Maven Artifact ID

## ステータス

採用

## 背景

`v0.1.0` の初回 GitHub Packages publish は build と integration test を通過したあと、main Maven publication の upload で失敗した。失敗した request は project name 由来の artifact ID `MapperForge` を含む path に対して行われていた。

GitHub Packages は local Maven publication より package coordinates に厳しい。公開 plugin ID は `io.github.isksss.mapperforge` のまま維持し、implementation artifact は lowercase artifact ID に固定することで、case-sensitive な package path 問題を避ける。

## 決定

`pluginMaven` publication の artifact ID を明示的に `mapperforge` に設定する。

Gradle plugin marker publication は plugin ID から生成されるものを維持し、implementation artifact は次を使う。

```text
io.github.isksss:mapperforge:<version>
```

## 結果

失敗した `v0.1.0` publish は再利用しない。次の publish attempt は、GitHub Packages 上の部分 upload と衝突しないように新しい release tag を使う。
