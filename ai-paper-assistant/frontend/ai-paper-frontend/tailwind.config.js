/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      // 编辑风格色彩体系 — 暖墨色 + 单点琥珀强调
      colors: {
        ink: {
          50:  '#f7f5f0',
          100: '#ede8de',
          200: '#d9cfbd',
          300: '#bfb098',
          400: '#a69373',
          500: '#8c7a5c',
          600: '#6b5d47',
          700: '#4a3f31',
          800: '#2d251c',
          900: '#1a1510',
        },
        amber: {
          DEFAULT: '#C2782F',
          light:   '#E8A84C',
          dark:    '#9B5E22',
        },
        surface: {
          paper:  '#FDFBF7',
          card:   '#F5F1E9',
          muted:  '#EBE5D9',
        },
        risk: {
          high:   '#C83E3E',
          medium: '#C2782F',
          low:    '#4A8C5C',
        }
      },

      // 字体层级（编辑风格：衬线标题 + 无衬线正文）
      fontFamily: {
        display: ['"Noto Serif SC"', '"Noto Serif"', 'Georgia', 'serif'],
        body:    ['"Inter"', '"Noto Sans SC"', 'system-ui', 'sans-serif'],
        mono:    ['"JetBrains Mono"', 'ui-monospace', 'monospace'],
      },

      // 间距节奏（基于 4px，更加宽松）
      spacing: {
        '18': '4.5rem',
        '22': '5.5rem',
        '30': '7.5rem',
      },

      // 圆角系统
      borderRadius: {
        'sm':    '4px',
        'md':    '8px',
        'lg':    '12px',
        'xl':    '16px',
        '2xl':   '24px',
      },

      // 阴影系统（柔和、有层次）
      boxShadow: {
        'card':    '0 1px 3px rgba(45, 37, 28, 0.06), 0 1px 2px rgba(45, 37, 28, 0.04)',
        'card-hover': '0 4px 16px rgba(45, 37, 28, 0.10), 0 2px 6px rgba(45, 37, 28, 0.06)',
        'drawer':  '-4px 0 24px rgba(45, 37, 28, 0.12)',
        'modal':   '0 8px 32px rgba(45, 37, 28, 0.16), 0 2px 8px rgba(45, 37, 28, 0.08)',
      },

      // 过渡动画
      transitionDuration: {
        '400': '400ms',
        '600': '600ms',
      },
    },
  },
  plugins: []
}
