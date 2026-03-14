package Engine.Render.HUD;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FlexContainer {
    public enum FlexDirection {
        ROW, COLUMN
    }

    public enum JustifyContent {
        START, END, CENTER, SPACE_BETWEEN, SPACE_AROUND
    }

    public enum AlignItems {
        START, END, CENTER, STRETCH
    }

    public static class FlexItem {
        public float flexGrow = 0f;
        public int flexBasis = -1;
        public int width = -1;
        public int height = -1;
        public Rectangle bounds = new Rectangle(0, 0, 0, 0);

        public FlexItem() {}

        public FlexItem(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public FlexItem(float flexGrow, int flexBasis) {
            this.flexGrow = flexGrow;
            this.flexBasis = flexBasis;
        }
    }

    private FlexDirection direction = FlexDirection.ROW;
    private JustifyContent justifyContent = JustifyContent.START;
    private AlignItems alignItems = AlignItems.STRETCH;
    private int gap = 0;
    private int padding = 0;
    private int containerWidth = 0;
    private int containerHeight = 0;
    private int containerX = 0;
    private int containerY = 0;
    private final List<FlexItem> items = new ArrayList<>();

    public FlexContainer() {}

    public FlexContainer direction(FlexDirection direction) {
        this.direction = direction;
        return this;
    }

    public FlexContainer justifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
        return this;
    }

    public FlexContainer alignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
        return this;
    }

    public FlexContainer gap(int gap) {
        this.gap = gap;
        return this;
    }

    public FlexContainer padding(int padding) {
        this.padding = padding;
        return this;
    }

    public FlexContainer setBounds(int x, int y, int width, int height) {
        this.containerX = x;
        this.containerY = y;
        this.containerWidth = width;
        this.containerHeight = height;
        return this;
    }

    public FlexContainer addItem(FlexItem item) {
        items.add(item);
        return this;
    }

    public FlexItem addItem(int width, int height) {
        FlexItem item = new FlexItem(width, height);
        items.add(item);
        return item;
    }

    public FlexItem addItem(float flexGrow, int flexBasis) {
        FlexItem item = new FlexItem(flexGrow, flexBasis);
        items.add(item);
        return item;
    }

    public void layout() {
        if (items.isEmpty()) return;

        int innerWidth = containerWidth - padding * 2;
        int innerHeight = containerHeight - padding * 2;
        int totalGap = gap * (items.size() - 1);

        if (direction == FlexDirection.ROW) {
            int fixedWidth = 0;
            float totalGrow = 0;
            for (FlexItem item : items) {
                if (item.width > 0) {
                    fixedWidth += item.width;
                } else if (item.flexBasis > 0) {
                    fixedWidth += item.flexBasis;
                } else {
                    totalGrow += item.flexGrow;
                }
            }

            int availableWidth = innerWidth - totalGap - fixedWidth;
            int x = containerX + padding;
            int y = containerY + padding;

            for (FlexItem item : items) {
                int itemWidth;
                if (item.width > 0) {
                    itemWidth = item.width;
                } else if (item.flexBasis > 0) {
                    itemWidth = item.flexBasis;
                } else if (totalGrow > 0 && availableWidth > 0) {
                    itemWidth = (int) (item.flexGrow / totalGrow * availableWidth);
                } else {
                    itemWidth = 0;
                }

                int itemHeight;
                if (item.height > 0) {
                    itemHeight = item.height;
                } else {
                    itemHeight = innerHeight;
                }

                int itemY = y;
                if (alignItems == AlignItems.CENTER) {
                    itemY = y + (innerHeight - itemHeight) / 2;
                } else if (alignItems == AlignItems.END) {
                    itemY = y + innerHeight - itemHeight;
                }

                item.bounds = new Rectangle(x, itemY, Math.max(0, itemWidth), Math.max(0, itemHeight));
                x += itemWidth + gap;
            }

            if (justifyContent == JustifyContent.CENTER && items.size() > 1) {
                int totalContentWidth = x - containerX - padding - gap;
                int offset = (innerWidth - totalContentWidth) / 2;
                for (FlexItem item : items) {
                    item.bounds.x += offset;
                }
            } else if (justifyContent == JustifyContent.END && items.size() > 1) {
                int totalContentWidth = x - containerX - padding - gap;
                int offset = innerWidth - totalContentWidth;
                for (FlexItem item : items) {
                    item.bounds.x += offset;
                }
            } else if (justifyContent == JustifyContent.SPACE_BETWEEN && items.size() > 1) {
                int totalContentWidth = x - containerX - padding - gap;
                int space = (innerWidth - totalContentWidth) / (items.size() - 1);
                int cx = containerX + padding;
                for (FlexItem item : items) {
                    item.bounds.x = cx;
                    cx += item.bounds.width + gap + space;
                }
            }
        } else {
            int fixedHeight = 0;
            float totalGrow = 0;
            for (FlexItem item : items) {
                if (item.height > 0) {
                    fixedHeight += item.height;
                } else if (item.flexBasis > 0) {
                    fixedHeight += item.flexBasis;
                } else {
                    totalGrow += item.flexGrow;
                }
            }

            int availableHeight = innerHeight - totalGap - fixedHeight;
            int x = containerX + padding;
            int y = containerY + padding;

            for (FlexItem item : items) {
                int itemHeight;
                if (item.height > 0) {
                    itemHeight = item.height;
                } else if (item.flexBasis > 0) {
                    itemHeight = item.flexBasis;
                } else if (totalGrow > 0 && availableHeight > 0) {
                    itemHeight = (int) (item.flexGrow / totalGrow * availableHeight);
                } else {
                    itemHeight = 0;
                }

                int itemWidth;
                if (item.width > 0) {
                    itemWidth = item.width;
                } else {
                    itemWidth = innerWidth;
                }

                int itemX = x;
                if (alignItems == AlignItems.CENTER) {
                    itemX = x + (innerWidth - itemWidth) / 2;
                } else if (alignItems == AlignItems.END) {
                    itemX = x + innerWidth - itemWidth;
                }

                item.bounds = new Rectangle(itemX, y, Math.max(0, itemWidth), Math.max(0, itemHeight));
                y += itemHeight + gap;
            }

            if (justifyContent == JustifyContent.CENTER && items.size() > 1) {
                int totalContentHeight = y - containerY - padding - gap;
                int offset = (innerHeight - totalContentHeight) / 2;
                for (FlexItem item : items) {
                    item.bounds.y += offset;
                }
            } else if (justifyContent == JustifyContent.END && items.size() > 1) {
                int totalContentHeight = y - containerY - padding - gap;
                int offset = innerHeight - totalContentHeight;
                for (FlexItem item : items) {
                    item.bounds.y += offset;
                }
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(containerX, containerY, containerWidth, containerHeight);
    }

    public List<FlexItem> getItems() {
        return items;
    }

    public FlexItem getItem(int index) {
        return items.get(index);
    }

    public void clear() {
        items.clear();
    }
}
