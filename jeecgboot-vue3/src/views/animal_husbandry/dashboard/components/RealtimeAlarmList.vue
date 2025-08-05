
<template>
  <a-card title="实时告警" :loading="loading" class="list-card">
    <a-list item-layout="horizontal" :data-source="alarmData">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta>
            <template #title>
              <a @click="handleAlarmClick(item)">{{ `耳标 ${item.earTagId}` }} - {{ item.alarmContent }}</a>
            </template>
          </a-list-item-meta>
          <template #actions>
            <a-tag :color="getAlarmLevelColor(item.alarmLevel)">{{
              getAlarmLevelText(item.alarmLevel)
            }}</a-tag>
          </template>
        </a-list-item>
      </template>
      <template v-if="!loading && alarmData.length === 0">
        <a-empty description="暂无告警" />
      </template>
    </a-list>
  </a-card>
</template>

<script lang="ts" setup>
  import { PropType } from 'vue';
  import { Card as ACard, List as AList, Tag as ATag, Empty as AEmpty } from 'ant-design-vue';
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
  });

  const emit = defineEmits(['item-click']);

  function getAlarmLevelColor(level: string) {
    return level === 'CRITICAL' ? 'error' : 'warning';
  }

  function getAlarmLevelText(level: string) {
    return level === 'CRITICAL' ? '严重' : '警告';
  }

  function handleAlarmClick(item: RecentAlarmVo) {
    emit('item-click', item.animalId);
  }
</script> 