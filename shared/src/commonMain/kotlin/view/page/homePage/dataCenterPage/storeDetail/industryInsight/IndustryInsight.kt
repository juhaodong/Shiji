package view.page.homePage.dataCenterPage.storeDetail.industryInsight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.composable.basic.layout.SmallSpacer


data class IndustryInsight(
    val title: String,
    val source: String,
    val date: String,
    val content: String // Add content property
)

// Sample data for Industry Insights with content
val industryInsights = listOf(
    IndustryInsight(
        "💻 餐饮行业数字化转型趋势",
        "餐饮资讯网",
        "2023-12-20",
        "随着科技的不断发展，餐饮行业正在经历一场数字化转型。越来越多的餐厅开始使用数字工具来提升运营效率、改善客户体验和拓展业务渠道。\n\n数字化转型为餐饮行业带来了诸多好处，例如：\n\n* 提升运营效率：通过使用数字点餐系统、库存管理系统等，餐厅可以减少人工成本，提高运营效率。\n* 改善客户体验：通过使用在线预订系统、客户关系管理系统等，餐厅可以为客户提供更加便捷、个性化的服务。\n* 拓展业务渠道：通过使用外卖平台、在线商城等，餐厅可以拓展业务渠道，增加收入来源。\n\n数字化转型是餐饮行业发展的必然趋势，餐厅应该积极拥抱数字化，才能在激烈的市场竞争中立于不败之地。"
    ),
    IndustryInsight(
        "📈 如何提升门店运营效率",
        "餐饮管理杂志",
        "2023-12-18",
        "门店运营效率是餐饮企业盈利能力的关键因素之一。提升门店运营效率可以帮助餐厅降低成本、提高利润率和提升客户满意度。\n\n以下是一些提升门店运营效率的实用技巧：\n\n* 优化门店布局：合理规划门店布局，可以提高员工的工作效率，减少客户等待时间。\n* 使用科技工具：使用数字点餐系统、库存管理系统等，可以自动化一些重复性的工作，提高运营效率。\n* 提升员工技能：对员工进行培训，提升他们的技能水平，可以提高工作效率和服务质量。\n* 建立标准流程：建立标准化的运营流程，可以减少人为错误，提高运营效率。\n\n通过采取以上措施，餐厅可以有效提升门店运营效率，实现盈利能力的提升。"
    ),
    IndustryInsight(
        "📊 最新餐饮消费趋势分析",
        "餐饮数据报告",
        "2023-12-15",
        "随着消费升级和生活方式的改变，餐饮消费趋势也在不断变化。了解最新的餐饮消费趋势，可以帮助餐厅更好地把握市场机遇，制定有效的经营策略。\n\n以下是一些最新的餐饮消费趋势：\n\n* 健康饮食：消费者越来越注重健康饮食，对食材的品质和营养价值要求更高。\n* 个性化需求：消费者对餐饮服务的需求更加个性化，希望能够获得定制化的服务体验。\n* 便利快捷：消费者对餐饮服务的便利性和快捷性要求更高，希望能够快速点餐、快速用餐。\n* 线上消费：线上消费已经成为餐饮消费的重要渠道，消费者越来越习惯于通过外卖平台、在线商城等进行餐饮消费。\n\n餐厅应该密切关注餐饮消费趋势的变化，及时调整经营策略，才能满足消费者不断变化的需求。"
    ),
    IndustryInsight(
        "📩 私域流量运营指南",
        "餐饮营销手册",
        "2023-12-12",
        "私域流量是餐饮企业的重要资产，可以帮助餐厅降低营销成本，提升客户忠诚度。\n\n以下是一些私域流量运营的实用技巧：\n\n* 建立私域流量池：通过微信公众号、小程序、社群等方式，建立自己的私域流量池。\n* 提供优质内容：为私域流量池的用户提供优质的内容，例如美食推荐、优惠活动等，吸引用户的关注。\n* 与用户互动：与私域流量池的用户进行互动，例如开展线上活动、收集用户反馈等，提升用户粘性。\n* 精准营销：根据用户的需求和喜好，进行精准营销，提升营销效果。\n\n通过有效运营私域流量，餐厅可以建立与客户的长期关系，提升客户忠诚度。"
    ),
    IndustryInsight(
        "🚚 餐饮供应链管理优化",
        "餐饮物流平台",
        "2023-12-10",
        "餐饮供应链管理是餐饮企业运营的重要环节，对餐厅的成本控制和食品安全至关重要。\n\n以下是一些餐饮供应链管理优化的实用技巧：\n\n* 选择优质供应商：选择信誉良好、产品质量稳定的供应商，可以保证食材的品质和安全。\n* 建立长期合作关系：与供应商建立长期合作关系，可以降低采购成本，提高供应链的稳定性。\n* 使用科技工具：使用供应链管理系统，可以实时跟踪库存情况，优化采购计划，提高供应链效率。\n* 加强食品安全管理：建立完善的食品安全管理体系，对食材进行严格的检验和监管，确保食品安全。\n\n通过优化餐饮供应链管理，餐厅可以降低成本，提高效率，保障食品安全。"
    ),
    IndustryInsight(
        "🚧 餐饮食品安全监管新规",
        "食品安全局",
        "2023-12-08",
        "餐饮食品安全关系到人民群众的身体健康和生命安全，是餐饮企业必须高度重视的问题。\n\n近年来，国家不断加强餐饮食品安全监管，出台了一系列新的监管法规。\n\n以下是一些餐饮食品安全监管新规：\n\n* 加强餐饮许可管理：对餐饮企业的许可证进行严格审查，提高餐饮企业的准入门槛。\n* 加强食品安全检查：对餐饮企业进行定期和不定期的食品安全检查，发现问题及时处理。\n* 加大处罚力度：对违反餐饮食品安全法规的企业，加大处罚力度，起到震慑作用。\n\n餐饮企业应该认真学习和遵守餐饮食品安全监管新规，切实履行食品安全主体责任，保障消费者的饮食安全。"
    ),
    IndustryInsight(
        "👨‍💼 餐饮行业人才招聘与培训",
        "餐饮人才网",
        "2023-12-05",
        "餐饮行业是劳动密集型行业，人才队伍建设是餐饮企业发展的关键。\n\n以下是一些餐饮行业人才招聘与培训的实用技巧：\n\n* 制定招聘计划：根据餐厅的实际需求，制定招聘计划，明确招聘岗位和人员数量。\n* 选择招聘渠道：选择合适的招聘渠道，例如招聘网站、人才市场等，发布招聘信息。\n* 筛选简历：对收到的简历进行筛选，选择符合条件的候选人进行面试。\n* 进行面试：通过面试，了解候选人的能力和素质，选择合适的员工。\n* 提供培训：对新员工进行培训，帮助他们快速适应工作环境，提升技能水平。\n\n通过有效的人才招聘与培训，餐厅可以建立一支高素质的人才队伍，为企业发展提供有力支撑。"
    ),
    IndustryInsight(
        "🔥 餐饮品牌建设与推广",
        "品牌营销咨询",
        "2023-12-03",
        "餐饮品牌是餐饮企业的核心竞争力，可以帮助餐厅提升知名度、吸引客户和提高利润率。\n\n以下是一些餐饮品牌建设与推广的实用技巧：\n\n* 明确品牌定位：根据餐厅的目标客户和市场定位，明确品牌定位，打造独特的品牌形象。\n* 设计品牌标识：设计具有识别度的品牌标识，例如logo、slogan等，提升品牌形象。\n* 开展品牌推广：通过各种渠道，例如广告、公关、活动等，开展品牌推广，提升品牌知名度。\n* 提供优质服务：为客户提供优质的服务，提升客户满意度，树立良好的品牌口碑。\n\n通过有效的品牌建设与推广，餐厅可以打造一个具有影响力的餐饮品牌，提升企业的竞争力。"
    ),
    IndustryInsight(
        "🎨 餐饮门店设计与装修",
        "餐饮空间设计",
        "2023-12-01",
        "餐饮门店设计与装修是餐饮企业的重要环节，对餐厅的品牌形象和客户体验至关重要。\n\n以下是一些餐饮门店设计与装修的实用技巧：\n\n* 明确设计风格：根据餐厅的品牌定位和目标客户，明确设计风格，打造独特的门店形象。\n* 合理规划布局：合理规划门店布局，可以提高员工的工作效率，减少客户等待时间。\n* 选择合适的材料：选择合适的装修材料，可以提升门店的档次和舒适度。\n* 注重细节设计：注重细节设计，例如灯光、音乐、装饰等，可以提升客户的用餐体验。\n\n通过精心的门店设计与装修，餐厅可以打造一个舒适、美观的用餐环境，提升客户满意度。"
    ),
    // ... (Add more Industry Insights with content)
)

@Composable
fun IndustryInsightItem(insight: IndustryInsight, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp, // Add elevation for visual distinction
        color = MaterialTheme.colorScheme.surfaceVariant // Set background color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Use a distinct color for the title
                )
                SmallSpacer()
                Text(
                    text = "${insight.source} - ${insight.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Use a distinct color for the source/date
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Read more",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}