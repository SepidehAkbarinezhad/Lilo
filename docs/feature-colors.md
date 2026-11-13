# Feature colors

The reference appearance is commit `4f7f599` (including its curved headers and bottom Save layout).

`LiloExtendedTheme.colors` supplies the feature palettes. `primary/onPrimary` are reserved for FABs and Save buttons; `primaryContainer/onPrimaryContainer` supply the soft list headers. Their fixed HCT tones are generated from the original sage, peach, and lavender seeds. The existing `accent`, `action`, and `onAccent` roles retain committed colors for all other controls.

Home deliberately retains its original palette. `HomeFeatureColors.kt` freezes the committed Compose Oklab interpolation and compositing results for cards, icon badges, inner reports, dividers, progress tracks, and checked boxes. Home text and icon colors remain the committed neutral/action tokens. No production Home palette calculation is performed.

| Feature | Light Home card | Dark Home card | Light header | Dark header | Light action | Dark action |
| --- | --- | --- | --- | --- | --- | --- |
| Tasks | #EBEEE0 | #2E322C | #D6E9B8 | #3C4C28 | #53643D | #BACD9E |
| Notes | #F7EAE2 | #332F2A | #FFDBCC | #603F2F | #7A5645 | #ECBCA7 |
| Passwords | #ECE5F0 | #303034 | #EDDCFF | #4F4064 | #68577D | #D3BEEA |

`OriginalHomeColorsTest` reproduces the committed calculations in test code and compares every fixed Home token at sRGB pixel precision. `FeatureColorsTest` checks the new header/action contrast and opaque fills. Full theme visual review is manual.
