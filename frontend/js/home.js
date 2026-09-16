/**
 * home.js — Home page specific interactions
 * Compass
 */

/* ============================================================
   HERO BACKGROUND LOAD ANIMATION
   ============================================================ */
(function initHeroBg() {
  const heroBg = document.getElementById('hero-bg');
  if (!heroBg) return;

  const img = new Image();
  img.src = 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=85&auto=format&fit=crop';
  img.onload = () => heroBg.classList.add('loaded');
})();

/* ============================================================
   PARTICLE CANVAS
   ============================================================ */
(function initParticles() {
  const canvas = document.getElementById('particles-canvas');
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  let particles = [];
  let animId;

  function resize() {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;
  }

  function createParticle() {
    return {
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      r: Math.random() * 1.8 + 0.4,
      dx: (Math.random() - 0.5) * 0.35,
      dy: -(Math.random() * 0.4 + 0.15),
      alpha: Math.random() * 0.5 + 0.1,
      color: Math.random() > 0.6 ? '#0ea5e9' : '#ffffff',
    };
  }

  function initParticlesArray() {
    const count = Math.min(Math.floor(canvas.width / 10), 90);
    particles = Array.from({ length: count }, createParticle);
  }

  function drawParticles() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    particles.forEach(p => {
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
      ctx.fillStyle = p.color;
      ctx.globalAlpha = p.alpha;
      ctx.fill();

      p.x += p.dx;
      p.y += p.dy;
      p.alpha -= 0.0008;

      if (p.alpha <= 0 || p.y < -10) {
        Object.assign(p, createParticle(), { y: canvas.height + 10, alpha: 0.1 });
      }
    });
    ctx.globalAlpha = 1;
    animId = requestAnimationFrame(drawParticles);
  }

  resize();
  initParticlesArray();
  drawParticles();

  window.addEventListener('resize', Utils.debounce(() => {
    resize();
    initParticlesArray();
  }, 200));

  // Stop animation when section not visible (performance)
  const heroSection = document.getElementById('home');
  if (heroSection) {
    const obs = new IntersectionObserver(entries => {
      entries.forEach(e => {
        if (!e.isIntersecting) {
          cancelAnimationFrame(animId);
        } else {
          drawParticles();
        }
      });
    }, { threshold: 0 });
    obs.observe(heroSection);
  }
})();

/* ============================================================
   HERO STAT COUNTER ANIMATION
   ============================================================ */
(function initCounters() {
  const stats = [
    { el: null, target: 50,   suffix: '+',  label: 'Destinations' },
    { el: null, target: 200,  suffix: '+',  label: 'Local Guides' },
    { el: null, target: 1000, suffix: '+',  label: 'Happy Travelers' },
  ];

  const statEls = document.querySelectorAll('.hero-stat-num');
  if (!statEls.length) return;

  function animateCounter(el, target, suffix) {
    const duration = 1600;
    const start    = performance.now();

    function update(now) {
      const progress = Math.min((now - start) / duration, 1);
      const ease     = 1 - Math.pow(1 - progress, 3); // ease-out cubic
      const current  = Math.floor(ease * target);
      el.textContent = current + suffix;
      if (progress < 1) requestAnimationFrame(update);
    }
    requestAnimationFrame(update);
  }

  const heroSection = document.getElementById('home');
  let counted = false;

  const obs = new IntersectionObserver(entries => {
    if (entries[0].isIntersecting && !counted) {
      counted = true;
      statEls.forEach((el, i) => {
        if (stats[i]) {
          const raw = el.textContent;
          const num = parseInt(raw.replace(/\D/g, ''), 10);
          const sfx = raw.replace(/\d/g, '').trim();
          if (!isNaN(num)) animateCounter(el, num, sfx);
        }
      });
    }
  }, { threshold: 0.5 });

  if (heroSection) obs.observe(heroSection);
})();

/* ============================================================
   DESTINATION CARD — Prefill planner on click
   ============================================================ */
(function initDestinationCards() {
  document.querySelectorAll('.destination-card[data-dest]').forEach(card => {
    card.addEventListener('click', e => {
      // Only navigate if not clicking the inner explore button
      if (e.target.closest('.btn')) return;
      const dest = card.dataset.dest;
      window.location.href = `planner.html?dest=${encodeURIComponent(dest)}&tab=explore`;
    });
    card.style.cursor = 'pointer';
  });
})();

/* ============================================================
   WELCOME TOAST (first visit)
   ============================================================ */
(function showWelcomeToast() {
  if (sessionStorage.getItem('st_welcomed')) return;
  setTimeout(() => {
    Toast.info('👋 Welcome! Enter your destination to get a personalised travel plan.', 5000);
    sessionStorage.setItem('st_welcomed', '1');
  }, 1800);
})();
