<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <section class="admin-placeholder">
    <p>后台能力建设</p>
    <h1>{{ route.meta.title }}</h1>
    <span>{{ route.meta.description || '该模块将在后续小阶段接入真实管理功能。' }}</span>
  </section>
</template>

<style scoped>
.admin-placeholder {
  min-height: 54vh;
  padding: 42px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: white;
  box-shadow: var(--shadow-card);
}

.admin-placeholder p {
  color: var(--color-cinnabar);
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.14em;
}

.admin-placeholder h1 {
  margin: 16px 0 10px;
  font-size: 32px;
}

.admin-placeholder span {
  color: var(--color-text-muted);
  line-height: 1.8;
}
</style>
