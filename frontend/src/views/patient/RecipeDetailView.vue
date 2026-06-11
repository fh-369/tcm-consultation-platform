<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

import { getPublishedRecipeDetail } from '../../api/content'

const route = useRoute()
const recipe = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try { recipe.value = await getPublishedRecipeDetail(route.params.id) }
  catch (error) { ElMessage.error(error.message || '药膳详情加载失败') }
  finally { loading.value = false }
})
</script>

<template>
  <section v-loading="loading" class="recipe-detail page-container">
    <RouterLink to="/recipes">← 返回药膳推荐</RouterLink>
    <article v-if="recipe">
      <header><span>{{ recipe.season }} · {{ recipe.constitution }}</span><h1>{{ recipe.name }}</h1><p>{{ recipe.summary }}</p></header>
      <div class="info-grid"><section><strong>适合人群</strong><p>{{ recipe.suitableFor }}</p></section><section><strong>食材准备</strong><p>{{ recipe.ingredients }}</p></section></div>
      <section class="steps"><strong>制作步骤</strong><p>{{ recipe.steps }}</p></section>
    </article>
  </section>
</template>

<style scoped>
.recipe-detail { min-height: 60vh; padding-block: 42px; }
.recipe-detail > a { color: var(--color-cinnabar); font-size: 12px; font-weight: 800; }
article { max-width: 900px; margin: 22px auto; padding: clamp(28px, 6vw, 64px); border: 1px solid var(--color-border); border-radius: var(--radius-lg); background: white; box-shadow: var(--shadow-soft); }
header span { color: var(--color-cinnabar); font-size: 11px; font-weight: 800; }
h1 { margin: 16px 0; font-size: clamp(2.4rem, 6vw, 4.8rem); letter-spacing: -.07em; }
p { color: var(--color-text-muted); line-height: 1.95; white-space: pre-wrap; }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-top: 38px; }
.info-grid section, .steps { padding: 22px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); background: var(--color-mist); }
.steps { margin-top: 14px; background: white; }
@media (max-width: 650px) { .info-grid { grid-template-columns: 1fr; } }
</style>
