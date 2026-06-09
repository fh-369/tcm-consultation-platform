<template>
  <div class="home-page">
    <section class="hero page-container">
      <div class="hero-copy">
        <p class="eyebrow">安心问诊 · 清晰跟进</p>
        <h1>让身体的变化<br />被认真听见</h1>
        <p class="hero-description">
          清楚记录症状与持续时间，获得有层次的提醒，并随时了解问诊处理进度。
        </p>
        <RouterLink class="primary-link" to="/consultation/new">开始在线问诊</RouterLink>
      </div>

      <div class="botanical-art" role="img" aria-label="现代草药植物插画">
        <span class="botanical-orbit orbit-one" aria-hidden="true"></span>
        <span class="botanical-orbit orbit-two" aria-hidden="true"></span>
        <span class="stem stem-one" aria-hidden="true"></span>
        <span class="stem stem-two" aria-hidden="true"></span>
        <span class="stem stem-three" aria-hidden="true"></span>
        <span v-for="leaf in 8" :key="leaf" :class="`leaf leaf-${leaf}`" aria-hidden="true"></span>
        <span v-for="berry in 3" :key="berry" :class="`berry berry-${berry}`" aria-hidden="true"></span>
        <span class="art-caption">艾草 · 薄荷 · 枸杞</span>
      </div>
    </section>

    <section class="home-sections page-container" aria-label="平台主要功能">
      <RouterLink class="feature feature-main" to="/consultation/new">
        <span class="feature-index">01</span>
        <div>
          <h2>在线问诊</h2>
          <p>从症状记录开始，建立清晰的问诊单。</p>
        </div>
        <span class="feature-action">进入问诊</span>
      </RouterLink>
      <RouterLink class="feature" to="/knowledge">
        <span class="feature-index">02</span>
        <h2>养生知识</h2>
        <p>浏览可信、已发布的日常养生内容。</p>
      </RouterLink>
      <RouterLink class="feature" to="/recipes">
        <span class="feature-index">03</span>
        <h2>药膳推荐</h2>
        <p>从季节和身体状态了解日常食养。</p>
      </RouterLink>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  padding-top: 32px;
}

.hero {
  position: relative;
  display: grid;
  min-height: 510px;
  grid-template-columns: 1.08fr 0.92fr;
  align-items: center;
  overflow: hidden;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #e1f0e8 0%, #f9fcfa 62%);
  box-shadow: var(--shadow-soft);
}

.hero-copy {
  position: relative;
  z-index: 2;
  padding: 60px;
}

.eyebrow {
  margin: 0;
  color: var(--color-jade);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.18em;
}

.hero h1 {
  margin: 18px 0;
  color: var(--color-ink);
  font-size: clamp(3rem, 6vw, 5.7rem);
  letter-spacing: -0.08em;
  line-height: 1.04;
}

.hero-description {
  max-width: 34em;
  margin: 0 0 28px;
  color: var(--color-text-muted);
  font-size: 16px;
  line-height: 1.9;
}

.botanical-art {
  position: relative;
  min-height: 510px;
  overflow: hidden;
  background:
    radial-gradient(circle at 50% 46%, rgb(255 255 255 / 78%), transparent 34%),
    linear-gradient(145deg, #cbe4d6, #72a98c);
}

.botanical-orbit {
  position: absolute;
  border: 1px solid rgb(255 255 255 / 46%);
  border-radius: 50%;
}

.orbit-one {
  width: 360px;
  height: 360px;
  top: 74px;
  right: 35px;
}

.orbit-two {
  width: 260px;
  height: 260px;
  top: 124px;
  right: 85px;
}

.stem {
  position: absolute;
  bottom: 70px;
  width: 5px;
  border-radius: 5px;
  background: #2e6f50;
  transform-origin: bottom;
}

.stem-one {
  right: 48%;
  height: 350px;
  transform: rotate(-8deg);
}

.stem-two {
  right: 34%;
  height: 305px;
  transform: rotate(16deg);
}

.stem-three {
  right: 61%;
  height: 260px;
  transform: rotate(-30deg);
}

.leaf {
  position: absolute;
  width: 92px;
  height: 42px;
  border: 1px solid rgb(255 255 255 / 24%);
  border-radius: 100% 0 100% 0;
  background: linear-gradient(135deg, #367957, #82b59a);
  box-shadow: inset 0 0 0 1px rgb(20 77 51 / 10%);
}

.leaf-1 { right: 41%; bottom: 335px; transform: rotate(27deg); }
.leaf-2 { right: 55%; bottom: 290px; transform: rotate(-44deg); }
.leaf-3 { right: 27%; bottom: 265px; transform: rotate(43deg); }
.leaf-4 { right: 49%; bottom: 218px; transform: rotate(-51deg); }
.leaf-5 { right: 23%; bottom: 178px; transform: rotate(38deg); }
.leaf-6 { right: 58%; bottom: 142px; transform: rotate(-56deg); }
.leaf-7 { right: 35%; bottom: 110px; transform: rotate(49deg); }
.leaf-8 { right: 65%; bottom: 235px; transform: rotate(-35deg); }

.berry {
  position: absolute;
  width: 20px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-cinnabar);
  box-shadow: 0 8px 16px rgb(126 48 35 / 24%);
}

.berry-1 { right: 24%; bottom: 300px; transform: rotate(-12deg); }
.berry-2 { right: 20%; bottom: 270px; transform: rotate(10deg); }
.berry-3 { right: 28%; bottom: 260px; transform: rotate(4deg); }

.art-caption {
  position: absolute;
  right: 28px;
  bottom: 24px;
  padding: 8px 10px;
  border: 1px solid rgb(255 255 255 / 46%);
  border-radius: var(--radius-sm);
  background: rgb(255 255 255 / 62%);
  color: var(--color-ink);
  font-size: 11px;
  letter-spacing: 0.12em;
}

.home-sections {
  display: grid;
  grid-template-columns: 1.35fr 1fr 1fr;
  gap: 12px;
  margin-top: 20px;
}

.feature {
  min-height: 190px;
  padding: 28px;
  border-top: 2px solid var(--color-jade);
  background: rgb(255 255 255 / 64%);
}

.feature:hover {
  border-color: var(--color-cinnabar);
  background: white;
  box-shadow: var(--shadow-card);
  transform: translateY(-3px);
}

.feature-main {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 0 20px;
  border-color: var(--color-cinnabar);
}

.feature-index {
  color: var(--color-cinnabar);
  font-size: 11px;
  font-weight: 800;
}

.feature h2 {
  margin: 17px 0 9px;
  font-size: 21px;
}

.feature-main h2 {
  margin-top: 0;
}

.feature p {
  margin: 0;
  color: var(--color-text-muted);
  font-size: 13px;
  line-height: 1.75;
}

.feature-action {
  align-self: end;
  grid-column: 2;
  margin-top: 22px;
  color: var(--color-cinnabar);
  font-size: 12px;
  font-weight: 800;
}

@media (max-width: 920px) {
  .hero {
    grid-template-columns: 1fr;
  }

  .hero-copy {
    padding: 42px 28px;
  }

  .botanical-art {
    min-height: 390px;
  }

  .home-sections {
    grid-template-columns: 1fr;
  }

  .feature {
    min-height: auto;
  }
}

@media (max-width: 620px) {
  .home-page {
    padding-top: 16px;
  }

  .hero h1 {
    font-size: 3rem;
  }

  .botanical-art {
    min-height: 320px;
  }
}
</style>
