<template>
  <div class="auth-shell">
    <RouterLink class="auth-brand" to="/" aria-label="返回岐黄问诊首页">
      <span aria-hidden="true">岐</span>
      <strong>岐黄问诊</strong>
    </RouterLink>

    <main class="auth-stage">
      <section class="auth-intro">
        <p>清楚记录 · 安心跟进</p>
        <h1>从一次认真登录<br />开始了解身体</h1>
        <div class="auth-botanical" aria-hidden="true">
          <i></i><i></i><i></i><i></i><i></i>
        </div>
      </section>

      <section class="auth-content">
        <RouterView />
      </section>
    </main>
  </div>
</template>

<style scoped>
.auth-shell {
  min-height: 100vh;
  padding: 24px;
  background: var(--color-mist);
}

.auth-brand {
  display: inline-flex;
  min-height: 48px;
  align-items: center;
  gap: 10px;
  color: var(--color-ink);
}

.auth-brand span {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 50%;
  background: var(--color-ink);
  color: white;
  font-weight: 800;
}

.auth-stage {
  display: grid;
  width: min(1040px, 100%);
  min-height: 620px;
  grid-template-columns: 1fr 0.88fr;
  margin: 26px auto 0;
  overflow: hidden;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: white;
  box-shadow: var(--shadow-soft);
}

.auth-intro {
  position: relative;
  overflow: hidden;
  padding: 68px 54px;
  background: linear-gradient(145deg, #d9ece2, #86b79e);
}

.auth-intro p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.16em;
}

.auth-intro h1 {
  position: relative;
  z-index: 2;
  margin: 20px 0;
  color: var(--color-ink);
  font-size: clamp(2.4rem, 5vw, 4rem);
  letter-spacing: -0.06em;
  line-height: 1.12;
}

.auth-botanical {
  position: absolute;
  right: -36px;
  bottom: -44px;
  width: 360px;
  height: 360px;
  border: 1px solid rgb(255 255 255 / 50%);
  border-radius: 50%;
}

.auth-botanical::before,
.auth-botanical::after {
  position: absolute;
  bottom: 38px;
  width: 4px;
  height: 270px;
  border-radius: 4px;
  background: #2d6f50;
  content: "";
  transform-origin: bottom;
}

.auth-botanical::before {
  left: 160px;
  transform: rotate(-18deg);
}

.auth-botanical::after {
  left: 220px;
  transform: rotate(17deg);
}

.auth-botanical i {
  position: absolute;
  z-index: 1;
  width: 90px;
  height: 38px;
  border-radius: 100% 0 100% 0;
  background: #4d916f;
}

.auth-botanical i:nth-child(1) { left: 82px; bottom: 210px; transform: rotate(-30deg); }
.auth-botanical i:nth-child(2) { left: 150px; bottom: 155px; transform: rotate(38deg); }
.auth-botanical i:nth-child(3) { left: 215px; bottom: 225px; transform: rotate(34deg); }
.auth-botanical i:nth-child(4) { left: 230px; bottom: 125px; transform: rotate(-42deg); }
.auth-botanical i:nth-child(5) { left: 105px; bottom: 95px; transform: rotate(42deg); }

.auth-content {
  display: grid;
  place-items: center;
  padding: 50px;
}

@media (max-width: 820px) {
  .auth-stage {
    grid-template-columns: 1fr;
  }

  .auth-intro {
    min-height: 240px;
    padding: 38px 28px;
  }

  .auth-botanical {
    right: -100px;
    bottom: -160px;
  }

  .auth-content {
    padding: 36px 24px;
  }
}
</style>
