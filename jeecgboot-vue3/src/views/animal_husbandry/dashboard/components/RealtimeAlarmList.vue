
<template>
  <a-card :title="title" :loading="loading" class="list-card">
    <template #extra>
      <a-button type="link" @click="handleMoreClick">更多</a-button>
    </template>
    <div class="scroll-container" v-if="alarmData.length > 0">
      <a-list item-layout="horizontal" :data-source="doubledAlarmData" class="scroll-list" :class="{ 'scrolling': startScrollAnimation }">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #title>
                <a @click="handleAlarmClick(item)">{{ `耳标 ${item.earTagId}` }} - {{ item.alarmContent }}</a>
              </template>
            </a-list-item-meta>
            <template #actions>
              <a-tag v-if="item.alarmLevel" :color="getAlarmLevelColor(item.alarmLevel)">{{
                getAlarmLevelText(item.alarmLevel)
              }}</a-tag>
            </template>
          </a-list-item>
        </template>
      </a-list>
    </div>
    <template v-if="!loading && alarmData.length === 0">
      <a-empty description="暂无告警" />
    </template>
  </a-card>
</template>

<script lang="ts" setup>
  import { PropType, computed, ref, watch, onMounted, onUnmounted, nextTick } from 'vue';
  import { Card as ACard, List as AList, Tag as ATag, Empty as AEmpty, Button as AButton } from 'ant-design-vue';
  import { RecentAlarmVo } from '../dashboard.model';

  const AListItem = AList.Item;
  const AListItemMeta = AList.Item.Meta;

  const props = defineProps({
    loading: {
      type: Boolean,
      default: true,
    },
    alarmData: {
      type: Array as PropType<RecentAlarmVo[]>,
      default: () => [],
    },
    title: {
      type: String,
      default: '实时告警',
    },
  });

  const emit = defineEmits(['item-click', 'more-click']);
  
  const MAX_ITEMS_VISIBLE = 4;
  const startScrollAnimation = ref(false);

  // 复制数据以实现无缝滚动
  const doubledAlarmData = computed(() => {
    if (props.alarmData.length > MAX_ITEMS_VISIBLE) {
      return [...props.alarmData, ...props.alarmData];
    }
    return props.alarmData;
  });
  
  // 监听数据变化以启动/停止动画
  watch(() => props.alarmData, (newData) => {
    // 使用 nextTick 确保DOM更新后再判断是否需要滚动
    nextTick(() => {
      startScrollAnimation.value = newData.length > MAX_ITEMS_VISIBLE;
    });
  }, { immediate: true });

  function getAlarmLevelColor(level: string) {
    return level === 'CRITICAL' ? 'error' : 'warning';
  }

  function getAlarmLevelText(level: string) {
    return level === 'CRITICAL' ? '严重' : '警告';
  }

  function handleAlarmClick(item: RecentAlarmVo) {
    emit('item-click', item.animalId);
  }

  function handleMoreClick() {
    emit('more-click', props.title);
  }
</script>

<style lang="less" scoped>
.list-card {
  // 固定卡片body的高度，为滚动创造条件
  :deep(.ant-card-body) {
    height: 220px; // 约4-5条item的高度
    padding: 0;
  }
}

.scroll-container {
  height: 100%;
  overflow: hidden;
  position: relative;
}

.scroll-list {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  
  &.scrolling {
    // 动画：从头滚动到一半（即原始数据末尾）
    animation: scroll-up linear infinite;
    animation-duration: calc(v-bind('props.alarmData.length') * 3s); // 根据条目数动态调整滚动速度

    &:hover {
      animation-play-state: paused;
    }
  }
}

@keyframes scroll-up {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(-50%);
  }
}

// 调整List Item的样式以适应滚动
:deep(.ant-list-item) {
  padding: 12px 24px;
}
</style> 