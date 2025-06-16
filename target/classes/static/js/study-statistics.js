/**
 * 学习进度统计功能
 * 用于显示用户学习条数和最近学习时间
 */

// 加载学习统计信息
function loadStudyStatistics() {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error('未登录，无法获取学习统计');
        return;
    }
    
    // 获取学习统计信息
    fetch('/study-records/statistics', {
        headers: {
            'Authorization': 'Bearer ' + token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('获取学习统计信息失败');
        }
        return response.json();
    })
    .then(data => {
        // 更新学习条数
        const studiedCountElement = document.getElementById('studied-count');
        if (studiedCountElement) {
            studiedCountElement.textContent = data.studiedCount || 0;
        }
        
        // 更新学习进度中的已学习条数
        const studiedCountProgressElement = document.getElementById('studied-count-progress');
        if (studiedCountProgressElement) {
            studiedCountProgressElement.textContent = data.studiedCount || 0;
        }
        
        // 更新学习条数说明
        const studiedCountInfoElement = document.getElementById('studied-count-info');
        if (studiedCountInfoElement) {
            studiedCountInfoElement.textContent = '(学习时间≥5秒钟的条文)';
        }
        
        // 更新最近学习时间
        const lastStudyTimeElement = document.getElementById('last-study-time');
        if (lastStudyTimeElement && data.lastStudyTime) {
            const lastStudyDate = new Date(data.lastStudyTime);
            lastStudyTimeElement.textContent = lastStudyDate.toLocaleString();
        } else if (lastStudyTimeElement) {
            lastStudyTimeElement.textContent = '暂无学习记录';
        }
        
        // 更新总学习时间
        const totalStudyTimeElement = document.getElementById('total-study-time');
        if (totalStudyTimeElement && data.totalStudyTime) {
            totalStudyTimeElement.textContent = data.totalStudyTime + ' 分钟';
        } else if (totalStudyTimeElement) {
            totalStudyTimeElement.textContent = '0 分钟';
        }
        
        // 获取总条文数量，用于计算学习进度
        getTotalPassagesCount(data.studiedCount);
    })
    .catch(error => {
        console.error('获取学习统计信息失败:', error);
    });
}

// 获取总条文数量并计算学习进度
function getTotalPassagesCount(studiedCount) {
    fetch('/passages/count', {
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('获取总条文数量失败');
        }
        return response.json();
    })
    .then(data => {
        const totalCount = data.count || 1; // 避免除以零
        const studiedCountValue = studiedCount || 0;
        
        // 计算学习进度百分比
        const progressPercentage = Math.min(100, Math.round((studiedCountValue / totalCount) * 100));
        
        // 更新进度条
        const progressBar = document.getElementById('study-progress');
        if (progressBar) {
            progressBar.style.width = progressPercentage + '%';
        }
        
        // 更新进度文本
        const progressText = document.getElementById('progress-percentage');
        if (progressText) {
            progressText.textContent = progressPercentage + '%';
        }
        
        // 更新总条文数量
        const totalPassagesElement = document.getElementById('total-passages');
        if (totalPassagesElement) {
            totalPassagesElement.textContent = totalCount;
        }
    })
    .catch(error => {
        console.error('获取总条文数量失败:', error);
    });
}

// 页面加载时执行
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否在学习统计页面
    const studyStatsContainer = document.getElementById('study-statistics-container');
    if (studyStatsContainer) {
        loadStudyStatistics();
    }
});