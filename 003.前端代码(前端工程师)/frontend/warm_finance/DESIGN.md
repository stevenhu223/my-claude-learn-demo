---
name: Warm Finance
colors:
  surface: '#fbf9f8'
  surface-dim: '#dbd9d9'
  surface-bright: '#fbf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f3'
  surface-container: '#efeded'
  surface-container-high: '#eae8e7'
  surface-container-highest: '#e4e2e2'
  on-surface: '#1b1c1c'
  on-surface-variant: '#534344'
  inverse-surface: '#303030'
  inverse-on-surface: '#f2f0f0'
  outline: '#867274'
  outline-variant: '#d9c1c2'
  surface-tint: '#944652'
  primary: '#944652'
  on-primary: '#ffffff'
  primary-container: '#ff9eaa'
  on-primary-container: '#7a323e'
  inverse-primary: '#ffb2ba'
  secondary: '#874e58'
  on-secondary: '#ffffff'
  secondary-container: '#ffb6c1'
  on-secondary-container: '#7b444e'
  tertiary: '#ad2c4e'
  on-tertiary: '#ffffff'
  tertiary-container: '#ff9ead'
  on-tertiary-container: '#90143a'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffd9dc'
  primary-fixed-dim: '#ffb2ba'
  on-primary-fixed: '#3e0312'
  on-primary-fixed-variant: '#762f3b'
  secondary-fixed: '#ffd9de'
  secondary-fixed-dim: '#fcb3be'
  on-secondary-fixed: '#360c17'
  on-secondary-fixed-variant: '#6b3741'
  tertiary-fixed: '#ffd9dd'
  tertiary-fixed-dim: '#ffb2bd'
  on-tertiary-fixed: '#400013'
  on-tertiary-fixed-variant: '#8c1037'
  background: '#fbf9f8'
  on-background: '#1b1c1c'
  surface-variant: '#e4e2e2'
  income-green: '#7DD87D'
  background-blush: '#FFF5F7'
  text-secondary: '#999999'
  surface-white: '#FFFFFF'
typography:
  display-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Be Vietnam Pro
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-sm:
    fontFamily: Be Vietnam Pro
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-md:
    fontFamily: Be Vietnam Pro
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-amount:
    fontFamily: JetBrains Mono
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 24px
  label-sm:
    fontFamily: Be Vietnam Pro
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  margin-mobile: 20px
  gutter: 12px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style

The brand identity is built on the pillars of **approachability, simplicity, and warmth**. It aims to transform the often-stressful task of financial tracking into a soothing, low-friction experience. The target audience includes individuals, couples, and small business owners who seek a personal touch in their digital tools.

The design style is a blend of **Soft Minimalism** and **Tactile UI**. It utilizes a "Kawaii-adjacent" aesthetic—meaning it is cute and friendly without being childish. The interface relies on generous whitespace, soft rounded corners, and a pastel-dominant palette to reduce cognitive load and evoke an emotional response of security and comfort. Physicality is introduced through subtle depth and squishy, high-radius components that feel delightful to interact with on a mobile screen.

## Colors

The palette is anchored in warm pink tones to differentiate from the cold blues and greens typical of traditional banking apps. 

- **Primary (#FF9EAA)**: Used for main actions and brand presence.
- **Secondary (#FFB6C1)**: Applied to soft container backgrounds and decorative elements.
- **Emphasis/Expense (#FF6B8A)**: Reserved for high-priority alerts, budget overruns, and critical "Expenditure" data.
- **Income (#7DD87D)**: A refreshing green used exclusively to represent financial growth and earnings.
- **Background (#FFF5F7)**: A tinted white that prevents the screen from feeling clinical, maintaining the "Warm" brand promise.
- **Text (#4A4A4A)**: A softened dark grey used for maximum readability without the harsh contrast of pure black.

## Typography

This design system uses **Be Vietnam Pro** for its humanist and friendly character, which perfectly complements the "warmth" of the brand. It is used for all interface copy to maintain a welcoming tone.

For financial data, **JetBrains Mono** is employed. As a monospaced font, it ensures that currency digits align perfectly in lists and reports, providing the structural precision needed for accounting while maintaining a modern, clean look.

- Use **display-lg** for monthly totals.
- Use **label-amount** for all currency displays to ensure tabular alignment.
- Maintain ample line height (1.5x) for body text to keep the "light and airy" feel.

## Layout & Spacing

The layout is **Mobile-First Fluid**, designed primarily for mobile browsers (320px - 428px). It uses a contextual layout model rather than a rigid grid, emphasizing clear vertical stacks and grouped information cards.

- **Margins**: A consistent 20px outer margin ensures content doesn't feel cramped against the screen edges.
- **Rhythm**: An 8px-based spacing system guides the vertical flow.
- **Safe Areas**: Critical actions (like the "Quick Record" button) should respect bottom-screen safe areas in mobile browsers.
- **Reflow**: On desktop previews, the content should center-align with a maximum width of 480px to maintain the "app-like" intimacy.

## Elevation & Depth

Depth is conveyed through **Tonal Layering** and **Soft Ambient Shadows**. 

The design avoids harsh borders. Instead, it uses high-contrast surface changes (e.g., a white card on a `#FFF5F7` background). Shadows are used sparingly and should be "tinted"—using a low-opacity version of the Primary color rather than grey—to keep the shadows feeling warm and integrated. 

Floating Action Buttons (FAB) for recording expenses use a slightly higher elevation with a larger blur radius to signify their primary importance and physical "pressability."

## Shapes

The shape language is defined by **High Circularity**. Large radii are used to remove any "sharpness" from the financial experience.

- **Buttons**: Use a 20px radius, creating a pill-like appearance for shorter buttons.
- **Cards**: Use a 16px radius to create a soft, friendly container for transaction lists and charts.
- **Inputs**: Use a 12px radius to balance form-factor with the roundness of the rest of the UI.
- **Icons**: Icons should be enclosed in circular backgrounds or have rounded terminals to match the visual language.

## Components

### Buttons
- **Primary**: Solid `#FF9EAA` with white text. 20px rounded corners. Use a subtle inner-glow for a tactile "squishy" feel.
- **Secondary**: Ghost style with `#FF9EAA` border or light `#FFB6C1` background.

### Cards
- Always white (`#FFFFFF`) with a 16px radius. 
- Use a soft pink-tinted drop shadow (e.g., `rgba(255, 158, 170, 0.1)`) to lift cards from the blush background.

### Input Fields
- 12px radius. 
- Background should be a very light grey or white with a 1px border in `#FFB6C1` when focused.
- Labels sit above the field in **label-sm** typography.

### Chips/Categories
- Pill-shaped (fully rounded).
- Use icon + text combinations. 
- Active states use the Primary color; inactive states use a very pale version of the secondary color.

### Financial Indicators
- **Income**: Text and icons in `#7DD87D`.
- **Expense**: Text and icons in `#FF6B8A`.
- **Progress Bars (Budget)**: Soft grey track with a `#FF9EAA` fill, turning `#FF6B8A` when exceeding 90%.

### Navigation
- A bottom tab bar with large, friendly icons. The "Add" button should be centered and emphasized with a circular background.