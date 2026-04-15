# Design System Document: High-End Editorial Android Experience

## 1. Overview & Creative North Star
### Creative North Star: "The Eternal Archive"
This design system is built to bridge the gap between ancient wisdom and modern digital utility. It moves away from the "utility-first" look of standard apps toward a "content-first" editorial experience. The goal is to make the user feel like they are stepping into a quiet, sunlit library rather than a noisy digital interface.

**Breaking the Template:**
To achieve a signature feel, we intentionally reject the rigid, boxed-in nature of standard Android apps. We utilize:
*   **Intentional Asymmetry:** Off-center text alignments for quotes and chapter introductions.
*   **Overlapping Elements:** Allowing typography to bleed slightly over image containers or surface shifts.
*   **High-Contrast Scales:** Dramatic differences between large, scholarly Serif titles and tiny, precise Sans-serif metadata.

---

## 2. Colors & Surface Philosophy
The palette is rooted in the organic tones of parchment and ink, providing a "spiritual" warmth that prevents the UI from feeling sterile.

### The "No-Line" Rule
**Explicit Instruction:** Designers are prohibited from using 1px solid borders to section content. Boundaries must be defined solely through background color shifts or tonal transitions.
*   *Implementation:* Use `surface_container_low` (#f5f3ef) to define a section sitting on a `surface` (#fbf9f5) background.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers—like stacked sheets of fine, heavy-weight paper.
*   **Level 0 (Base):** `surface` (#fbf9f5) - The main canvas.
*   **Level 1 (Sections):** `surface_container` (#efeeea) - Sub-sections or sidebars.
*   **Level 2 (In-Page Content):** `surface_container_high` (#eae8e4) - Cards or interactive widgets.
*   **Level 3 (Floating/Active):** `surface_container_lowest` (#ffffff) - Active inputs or high-focus items.

### The "Glass & Gradient" Rule
To add visual "soul," use subtle gradients for CTAs rather than flat fills. Transition from `primary` (#031632) to `primary_container` (#1a2b48) to give a slight 3D "ink-well" depth. For floating navigation or top bars, apply **Glassmorphism**: use a semi-transparent `surface` color with a 20px backdrop blur to allow content to softly bleed through the header.

---

## 3. Typography
Typography is the voice of this system. We pair the authoritative **Noto Serif** with the functional **Inter**.

*   **Display & Headline (Noto Serif):** Used for chapter titles, significant quotes, and spiritual headings. These should feel monumental.
*   **Body (Noto Serif):** Used for long-form study. The serif ensures high legibility for deep reading and evokes a scholarly manuscript.
*   **Label & Title (Inter):** Used for UI controls, buttons, and metadata (e.g., "3 min read" or "Source: Rashi"). The sans-serif provides a "modern anchor" to the scholarly text.

**Hierarchy Strategy:**
Use `display-lg` (3.5rem) for opening "Torah Portions" to create an editorial impact, immediately followed by `label-md` for metadata to create a sophisticated tension between large and small scales.

---

## 4. Elevation & Depth
In this design system, depth is a whisper, not a shout. We move away from traditional Material shadows.

### The Layering Principle
Depth is achieved by "stacking" the surface-container tokens. Place a `surface_container_lowest` (#ffffff) card on a `surface_container_low` (#f5f3ef) background. This creates a soft, natural lift without the need for an artificial shadow.

### Ambient Shadows
If an element *must* float (e.g., a FAB or a temporary modal), use **Ambient Shadows**:
*   **Blur:** 24dp - 40dp (Extra-diffused).
*   **Opacity:** 4%-6%.
*   **Tint:** Use a tinted version of `on_surface` (#1b1c1a) rather than pure black to keep the warmth of the parchment background intact.

### The "Ghost Border" Fallback
If a border is required for accessibility, use a "Ghost Border": the `outline_variant` token at **15% opacity**. 100% opaque borders are forbidden.

---

## 5. Components

### Cards & Content Lists
*   **Styling:** Radius set to `xl` (1.5rem / 24dp). No borders.
*   **Separation:** Do not use divider lines. Separate list items using vertical whitespace from the spacing scale (e.g., 24dp padding) or a subtle background shift to `surface_container_low`.

### Buttons
*   **Primary:** Deep Navy (#031632) with white text. Rounded corners at `full` (pill-shape) or `xl`.
*   **Secondary/Tertiary:** Instead of an outline, use a subtle `surface_container_highest` background or a simple text-link style using `tertiary_fixed_dim` (#e9c176) for a muted gold accent.

### Scholarly Commentary Drawers
Unique to this context, commentary should slide in as a "layered sheet." Use a `surface_container_low` background with a `surface_container_lowest` inner card to separate the commentary from the main Torah text.

### Inputs & Search
*   **Style:** Minimalist. No box container. Use a single `outline_variant` line at the bottom, or a very soft `surface_container` pill shape. Use Inter `label-md` for helper text.

---

## 6. Do's and Don'ts

### Do:
*   **Embrace Whitespace:** Treat negative space as a luxury. It allows the spiritual content to breathe.
*   **Use Tonal Shifts:** Rely on the difference between #FDFBF7 and #FAF9F6 to define areas.
*   **Prioritize Typography:** Let the Serif font do the heavy lifting of the brand identity.

### Don't:
*   **Don't Use Pure Black:** Use `primary` (#031632) or `on_background` (#1b1c1a) for text to maintain a premium, editorial feel.
*   **Don't Use Divider Lines:** Lines "cut" the soul of the page. Use spacing and color blocks instead.
*   **Don't Use Sharp Corners:** Avoid anything below `md` (0.75rem) rounding. Everything should feel soft and approachable.
*   **Don't Overuse Gold:** The `tertiary` gold (#C5A059) is a "highlight," not a primary UI color. Use it for active states, icons, or special accents only.
