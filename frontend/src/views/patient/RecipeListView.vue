<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { getPublishedRecipes } from '../../api/content'

const recipes = ref([])
const loading = ref(false)
const season = ref('')
const constitution = ref('')
const seasons = computed(() => [...new Set(recipes.value.map((item) => item.season).filter(Boolean))])
const constitutions = computed(() => [...new Set(recipes.value.map((item) => item.constitution).filter(Boolean))])
const visibleRecipes = computed(() => recipes.value.filter((item) =>
  (!season.value || item.season === season.value) &&
  (!constitution.value || item.constitution === constitution.value),
))

onMounted(async () => {
  loading.value = true
  try { recipes.value = await getPublishedRecipes() }
  catch (error) { ElMessage.error(error.message || '药膳加载失败') }
  finally { loading.value = false }
})
</script>

<template>
  <section class="recipe-page page-container">
    <header><p>顺季而食 · 温和调养</p><h1>药膳推荐</h1><span>按季节和体质筛选已发布药膳。</span></header>
    <div class="filters">
      <el-select v-model="season" clearable placeholder="全部季节"><el-option v-for="item in seasons" :key="item" :label="item" :value="item" /></el-select>
      <el-select v-model="constitution" clearable placeholder="全部体质"><el-option v-for="item in constitutions" :key="item" :label="item" :value="item" /></el-select>
    </div>
    <div v-loading="loading" class="recipe-grid">
      <RouterLink v-for="recipe in visibleRecipes" :key="recipe.id" :to="`/recipes/${recipe.id}`" class="recipe-card">
        <div class="recipe-mark">{{ recipe.name?.slice(0, 1) }}</div>
        <div><span>{{ recipe.season || '四季' }} · {{ recipe.constitution || '日常调养' }}</span><h2>{{ recipe.name }}</h2><p>{{ recipe.summary }}</p><small>适合：{{ recipe.suitableFor || '一般人群' }}</small></div>
      </RouterLink>
      <el-empty v-if="!loading && visibleRecipes.length === 0" description="暂无符合条件的药膳" />
    </div>
  </section>
</template>

<style scoped>
.recipe-page { padding-block: 48px 20px; }
header { display: grid; grid-template-columns: 1fr auto; align-items: end; padding-bottom: 24px; border-bottom: 1px solid var(--color-border); }
header p { grid-column: 1 / -1; margin: 0 0 8px; color: var(--color-cinnabar); font-size: 11px; font-weight: 800; letter-spacing: .15em; }
header h1 { margin: 0; font-size: clamp(2.5rem, 6vw, 5rem); letter-spacing: -.07em; }
header span { color: var(--color-text-muted); font-size: 13px; }
.filters { display: flex; gap: 10px; margin: 22px 0; }
.filters .el-select { width: 190px; }
.recipe-grid { display: grid; min-height: 220px; grid-template-columns: repeat(2, 1fr); gap: 14px; }
.recipe-card { display: grid; grid-template-columns: 120px 1fr; gap: 24px; min-height: 220px; padding: 22px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: white; }
.recipe-card:hover { border-color: var(--color-jade); transform: translateY(-3px); }
.recipe-mark { display: grid; place-items: center; border-radius: var(--radius-sm); background: linear-gradient(145deg, var(--color-jade-light), #b9d8c7); color: var(--color-ink); font-size: 48px; font-weight: 800; }
.recipe-card span, .recipe-card small { color: var(--color-cinnabar); font-size: 10px; font-weight: 800; }
.recipe-card h2 { margin: 18px 0 8px; font-size: 22px; }
.recipe-card p { color: var(--color-text-muted); font-size: 13px; line-height: 1.8; }
.recipe-card small { color: var(--color-jade); }
@media (max-width: 850px) { .recipe-grid { grid-template-columns: 1fr; } header { display: block; } header span { display: block; margin-top: 12px; } }
@media (max-width: 560px) { .recipe-card { grid-template-columns: 1fr; } .recipe-mark { min-height: 100px; } }
</style>
