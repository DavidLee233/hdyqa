<template>
  <!-- 核动力电脑 32.5 16 39   87   -->
  <!-- 家里面电脑 31.5 18 34.5 82.5 -->
  <div class="app-container">
    <el-row style="display: flex; height: 100vh; overflow: hidden;">
      <el-col style="flex: 0 0 36%; height: 100%; padding-right: 0px; overflow-y: auto;">
        <el-row :gutter="0" style="margin-bottom: 0px; height: 38vh; overflow: hidden;">
           <!-- 普通成员按钮 -->
          <el-row :gutter="10" class="mb8" :style="{height: '2vh',  marginTop: '0px'}">
            <el-col :span="1.5">
              <el-button type="info" plain icon="el-icon-answer" size="mini" :disabled="single" @click="handleAnswer">回答</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" plain icon="el-icon-refresh" size="mini" @click="handleRefreshCache">刷新缓存</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-switch
                v-model="openNotify"
                active-text=""
                inactive-text="消息提醒"
                @change="(openNotify) => openNotify ? openMessage() : closeMessage()">
              </el-switch>
            </el-col>
          </el-row>
          <!-- 左侧问题和回答角色表格 -->
          <el-table height="31.5vh" stripe border v-loading="loading" :data="qaList" @selection-change="handleSelectionChange" class="custom-bordered-table" style="width: 100%">
            <el-table-column type="selection" width="40" align="center"/>
            <el-table-column label="序号" type="index" width="50" align="center">
              <template slot-scope="scope">{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</template>
            </el-table-column>
            <el-table-column label="用户姓名" prop="createBy" align="center" />
            <el-table-column label="用户工号"  prop="createId" align="center"/>
            <el-table-column label="用户电话"  prop="telephone" align="center"/>
            <el-table-column label="问题标题" prop="title" align="center" :show-overflow-tooltip="true" />
            <el-table-column label="问题内容" prop="questionContent" align="center" :show-overflow-tooltip="true" />
            <el-table-column label="问题状态"  prop="status" align="center">
              <template slot-scope="scope">
                <span :class="`status-${scope.row.status}`">
                  {{ scope.row.status }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="问题类别" prop="type" width="110" align="center"/>
            <el-table-column label="严重程度"  prop="severity" align="center"/>
            <el-table-column label="操作" align="center" width="60" fixed="right" >
            <template slot-scope="scope">
              <el-col :span="1.5">
                <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button @click="handleRemind(scope.row)" type="text" size="small">提醒</el-button>
              </el-col>
            </template>
          </el-table-column>
          </el-table>
          <el-pagination
            v-show="total > 0"
            class="pagination-wrapper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryParams.pageNum"
            :page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </el-row>

        <!-- 左下一行展示问题窗口 -->
        <el-row :gutter="5" style="height: 18vh; margin-bottom: 0px;">
          <div class="custom-bordered-table paper-yellow" style="height: 100%; width: 100%; padding: 15px;">
            <!-- 第一行：问题标题 -->
            <div class="info-row">
              <span style="font-size: 12px; font-weight: bold;">问题标题：</span>
              <span style="font-size: 12px;">{{ selectedQuestion.title || '未选择问题' }}</span>
            </div>
            <!-- 第二行：问题标题 -->
            <div class="info-row" style="margin-bottom: 5px;">
              <span style="font-size: 12px; font-weight: bold;">问题内容：</span>
              <span style="font-size: 12px;">{{ selectedQuestion.questionContent || '暂无内容' }}</span>
            </div>
            <div class="info-row">
              <span style="font-size: 12px; font-weight: bold;">回答内容：</span>
              <span style="font-size: 12px;">{{ selectedQuestion.answerContent || '暂无回答' }}</span>
            </div>
            <!-- 第三行：用户姓名、用户工号、用户电话、房间号 -->
            <el-row :gutter="8" style="margin-bottom: 5px;">
              <el-col :span="5">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">用户姓名：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.createBy || '' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">用户工号：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.createId || '' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">用户电话：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.telephone || '' }}</span>
                </div>
              </el-col>
              <el-col :span="5">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">房间号：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.roomNumber || '' }}</span>
                </div>
              </el-col>
            </el-row>
            <!-- 第四行：用户部门、问题类别、问题状态、回答用户 -->
            <el-row :gutter="8" style="margin-bottom: 5px;">
              <el-col :span="5">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">用户部门：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.department || '' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">问题类别：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.type || '' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">问题状态：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.status || '' }}</span>
                </div>
              </el-col>
              <el-col :span="5">
                <div class="info-row">
                  <span style="font-size: 12px; font-weight: bold;">回答用户：</span>
                  <span style="font-size: 12px;">{{ selectedQuestion.updateBy || '' }}</span>
                </div>
              </el-col>
            </el-row>
            <!-- 第五行：问题附件 -->
            <div class="info-row" style="margin-bottom: 5px; display: flex; align-items: flex-start;">
              <span style="font-size: 12px; font-weight: bold; min-width: 60px; line-height: 24px;">问题附件：</span>
              <div style="flex: 1; display: flex; flex-wrap: wrap; align-items: center; margin-top: 2px;">
                <template v-if="selectedQuestion.questionAttachList && selectedQuestion.questionAttachList.length > 0">
                  <el-tooltip v-for="(file, index) in selectedQuestion.questionAttachList"
                              :key="'question-'+index"
                              placement="top"
                              :content="file.name">
                    <el-tag
                      size="small"
                      style="margin-right: 8px; margin-bottom: 8px; cursor: pointer; max-width: 200px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap; height: 24px; line-height: 22px;"
                      @click="handlePreview(file)"
                      closable
                      @close="handleRemoveAttachment(file, 'question')">
                      {{ truncateFilename(file.name, 10) }}
                    </el-tag>
                  </el-tooltip>
                </template>
                <span v-else style="font-size: 12px; line-height: 24px;">无</span>
              </div>
            </div>

            <!-- 第六行：回答附件 -->
            <div class="info-row" style="margin-bottom: 5px; display: flex; align-items: flex-start;">
              <span style="font-size: 12px; font-weight: bold; min-width: 60px; line-height: 24px;">回答附件：</span>
              <div style="flex: 1; display: flex; flex-wrap: wrap; align-items: center; margin-top: 2px;">
                <template v-if="selectedQuestion.answerAttachList && selectedQuestion.answerAttachList.length > 0">
                  <el-tooltip v-for="(file, index) in selectedQuestion.answerAttachList"
                              :key="'answer-'+index"
                              placement="top"
                              :content="file.name">
                    <el-tag
                      size="small"
                      style="margin-right: 8px; margin-bottom: 8px; cursor: pointer; max-width: 200px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap; height: 24px; line-height: 22px;"
                      @click="handlePreview(file)"
                      closable
                      @close="handleRemoveAttachment(file, 'answer')">
                      {{ truncateFilename(file.name, 10) }}
                    </el-tag>
                  </el-tooltip>
                </template>
                <span v-else style="font-size: 12px; line-height: 24px;">无</span>
              </div>
            </div>
          </div>
        </el-row>

        <!-- 左下二行讨论区窗口 -->
        <el-row :gutter="0" style="height: 34.5vh; overflow: hidden;">
          <div class="forum-container custom-bordered-table" style="height: 100%; width: 100%; padding: 10px; display: flex; flex-direction: column; background-color: #f0fff0;">
            <!-- 讨论区标题 -->
            <div style="text-align: center; margin-bottom: 0px; padding-right: 30px;">
              <span style="font-weight: bold; font-size: 16px; color: #333;">讨论区</span>
            </div>

            <!-- 回复列表 -->
            <div class="reply-list" style="flex: 1; overflow-y: auto; margin-bottom: 2px;" ref="replyListScroll">
              <div v-if="replyLoading" style="text-align: center; padding: 10px;"><i class="el-icon-loading"></i> 加载中...</div>
              <div v-if="replies.length === 0" class="empty-replies"
                  style="position: absolute;
                          top: 50%;
                          left: 50%;
                          transform: translate(-50%, -50%);
                          color: #909399;
                          font-size: 12px;
                          width: 100%;
                          text-align: center;">
                暂无回复，快来发表第一条评论吧！
              </div>
              <!-- 回复列表内容 -->
              <div v-for="(reply, index) in replies" :key="reply.replyId" class="reply-item"
                :id="`reply-${reply.replyId}`"
                style="padding: 5px 10px; border-bottom: 1px dashed #ebeef5; position: relative; min-height: 40px;">
                <div v-if="reply.isTemp" style="font-size: 10px; color: #999;">发送中...</div>
                <!-- 楼层信息 -->
                <div style="font-size: 11px; color: #909399; margin-bottom: 2px; line-height: 1.2;">
                  <span>#{{ (queryReply.pageNum - 1) * queryReply.pageSize + index + 1 }}</span>
                  <span style="margin-left: 8px;">{{ reply.createBy }}</span>
                  <span style="margin-left: 8px;">{{ parseTime(reply.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                  <span v-if="reply.replyToId" style="margin-left: 8px; color: #67C23A;">
                    <template v-if="floorCache[reply.replyToId] || getReplyFloorNumber(reply.replyToId) && getReplyFloorNumber(reply.replyToId) !== '引用楼层已删除'">
                      回复<a href="javascript:;" @click="scrollToReply(reply.replyToId)" style="color: #67C23A; text-decoration: underline;">#{{
                        floorCache[reply.replyToId] || getReplyFloorNumber(reply.replyToId)}}</a>
                    </template>
                    <template v-else>
                      回复 #已删楼
                    </template>
                  </span>
                </div>
                <!-- 回复内容 -->
                <div style="font-size: 12px; margin-bottom: 2px; word-break: break-word; line-height: 1.4;">
                  {{ reply.content }}
                </div>
                <!-- 操作按钮 -->
                <div style="text-align: right; position: absolute; bottom: 2px; right: 5px;">
                  <el-button type="text" size="mini" style="padding: 0 5px; font-size: 11px;" @click="handleReplyTo(reply)">
                    回复
                  </el-button>
                  <el-button type="text" size="mini" style="padding: 0 5px; font-size: 11px;" @click="handleReplyDel(reply)">
                    删除
                  </el-button>
                  <el-button type="text" size="mini" :disabled="reply.replyLikeCooldown" style="padding: 0 5px; font-size: 11px;" @click="handleThumbsUp(reply, 3000)">
                    <i class="el-icon-thumb" style="margin-right: 2px;"></i>
                    {{ reply.thumbsUp || 0 }}
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 分页控件 -->
            <el-pagination
              v-show="replyTotal > 0"
              class="pagination-wrapper"
              small
              layout="total, prev, pager, next"
              :page-size="queryReply.pageSize"
              :current-page="queryReply.pageNum"
              :total="replyTotal"
              @current-change="handleReplyCurrentChange"
              @size-change="handleReplySizeChange"
              style="padding: 5px 0; text-align: center;"
            >
            </el-pagination>

            <!-- 回复输入框 -->
            <div class="reply-input" style="border-top: 1px solid #dcdfe6; padding: 5px 0; display: flex; align-items: center;">
              <el-input
                type="text"
                placeholder="请输入回复内容"
                v-model="replyInputContent"
                @keyup.enter.native="handleSendReply"
                :disabled="!selectedQuestion.recordId"
                style="flex: 1; margin-right: 8px; height: 28px;"
                size="small">
              </el-input>
              <el-button
                type="primary"
                size="small"
                @click="handleSendReply"
                style="height: 28px;"
                :disabled="!replyInputContent || !selectedQuestion.recordId">
                发送
              </el-button>
            </div>
          </div>
        </el-row>
      </el-col>

      <!-- 分割线 -->
      <div style="width: 1px; background-color: #dcdfe6; margin: 0 5px;"></div>

      <el-col style="flex: 1; height: 100%; overflow-y: auto;">
        <!-- 管理员按钮 -->
        <el-row :gutter="10" class="mb8" :style="{height: '2vh',  marginTop: '0px'}">
          <el-col :span="1.5">
            <el-button type="danger" plain icon="el-icon-refresh" size="mini" @click="handleRefreshCache">刷新缓存</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-dropdown @command="handleSortCommand">
              <el-button type="primary" plain size="mini">排序方式<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="createTime" :class="{ 'is-active': orderByColumn === 'createTime' }">按创建时间排序
                  <i :class="orderByColumn === 'createTime' ? (isAsc === 'desc' ? 'el-icon-sort-down' : 'el-icon-sort-up') : ''"></i>
                </el-dropdown-item>
                <el-dropdown-item command="solveTime" :class="{ 'is-active': orderByColumn === 'solveTime' }">按解决时间排序
                  <i :class="orderByColumn === 'solveTime' ? (isAsc === 'desc' ? 'el-icon-sort-down' : 'el-icon-sort-up') : ''"></i>
                </el-dropdown-item>
                <el-dropdown-item command="viewCount" :class="{ 'is-active': orderByColumn === 'viewCount' }">按访问热度排序
                  <i :class="orderByColumn === 'viewCount' ? (isAsc === 'desc' ? 'el-icon-sort-down' : 'el-icon-sort-up') : ''"></i>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>

          <el-col :span="1.5">
            <el-dropdown @command="handleReadCommand">
              <el-button type="danger" plain size="mini">{{ filterSelf ? '仅查看自己' : '查看全部'}}<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="self">仅查看自己</el-dropdown-item>
                <el-dropdown-item command="all">查看全部</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>

          <right-toolbar :showSearch.sync="showSearch" :searchValue.sync="searchQueryParams.searchValue" @queryTable="getSearchList"></right-toolbar>
        </el-row>
        <!-- 右侧总表格 -->
        <el-table height="82.5vh" stripe border v-loading="admin_loading" :data="searchQaList" @selection-change="handleAdminSelectionChange"
                  class="custom-bordered-table" style="width: 100%">
          <el-table-column type="selection" width="40" align="center" />
          <el-table-column label="序号" type="index" width="50" align="center">
            <template slot-scope="scope">{{(searchQueryParams.pageNum - 1) * searchQueryParams.pageSize + scope.$index + 1}}</template>
          </el-table-column>
          <!-- <el-table-column label="提问者姓名" width="100" align="center" prop="createBy"/>
          <el-table-column label="提问者工号" width="100" align="center" prop="createId"/>
          <el-table-column label="提问者部门" width="100" align="center" prop="department"/>
          <el-table-column label="房间号" width="80" align="center" prop="roomNumber"/>
          <el-table-column label="电话号" width="100" align="center" prop="telephone"/> -->
          <el-table-column label="问题密级" width="80" align="center" prop="classification"/>
          <el-table-column label="问题标题" align="center" prop="title" :show-overflow-tooltip="true" />
          <el-table-column label="问题内容" align="center" prop="questionContent" :show-overflow-tooltip="true" />
          <el-table-column label="问题状态"  prop="status" align="center">
            <template slot-scope="scope">
                <span :class="`status-${scope.row.status}`">
                  {{ scope.row.status }}
                </span>
            </template>
          </el-table-column>
          <el-table-column label="问题类别" width="120" align="center" prop="type"/>
          <el-table-column label="回答内容" align="center" prop="answerContent" :show-overflow-tooltip="true" />
          <!-- <el-table-column label="回答者工号" width="100" align="center" prop="updateId" />
          <el-table-column label="回答者姓名" width="100" align="center" prop="updateBy"/> -->
          <el-table-column label="严重程度" width="80" align="center" prop="severity"/>
          <el-table-column label="是否可解决" width="100" align="center" prop="canSolve"/>
          <el-table-column label="创建时间" width="110" align="center" prop="createTime"/>
          <el-table-column label="完结时间" width="110" align="center" prop="solveTime"/>
          <el-table-column label="访问量" width="100" align="center" prop="visits"/>
          <el-table-column label="操作" align="center" width="80" fixed="right">
            <template slot-scope="scope">
              <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-show="searchTotal > 0"
          class="pagination-wrapper"
          @size-change="handleSearchSizeChange"
          @current-change="handleSearchCurrentChange"
          :current-page="searchQueryParams.pageNum"
          :page-size="searchQueryParams.pageSize"
          :page-sizes="[18, 36, 90, 180]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="searchTotal">
        </el-pagination>

      </el-col>
    </el-row>

    <!-- 图片预览弹窗 -->
    <el-image-viewer v-if="showImageViewer" :on-close="closeImageViewer" :url-list="[previewImageUrl]"/>

    <!-- 在线附件转换状态弹窗 -->
    <el-dialog
      :visible.sync="isConverting"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      width="30%"
      center>
      <div style="text-align: center">
        <el-progress
          v-if="convertProgress > 0"
          :percentage="convertProgress"
          :status="convertProgress === 100 ? 'success' : ''"
          style="margin-bottom: 15px"/>
        <p>{{ convertMessage }}</p>
        <i class="el-icon-loading" style="font-size: 24px; margin-top: 10px"></i>
      </div>
    </el-dialog>

    <!-- 问题回答表单 -->
    <el-dialog :title="answer_title" :visible.sync="answer_open" width="500px" v-dialogDrag append-to-body>
      <el-form ref="answer_form" :model="answer_form" :rules="answerRules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="用户姓名" prop="createBy">
              <el-input v-model="answer_form.createBy" :disabled="true" placeholder="请输入用户姓名"/>
            </el-form-item>
          </el-col>
          <el-col :span="14">
            <el-form-item label="用户工号" prop="createId">
              <el-input v-model="answer_form.createId" :disabled="true" placeholder="请输入用户工号"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="answer_form.roomNumber" :disabled="true" placeholder="请输入房间号"/>
            </el-form-item>
          </el-col>
          <el-col :span="14">
            <el-form-item label="用户电话" prop="telephone">
              <el-input v-model="answer_form.telephone" :disabled="true" placeholder="请输入电话号码"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="密级" prop="classification">
              <el-select v-model="answer_form.classification" placeholder="请选择密级" value-key="securityId" @change="handleAnswerSecurityChange" filterable :disabled="true">
                <el-option v-for="item in securityOptions" :key="item.securityId" :label="item.securityName" :value="item"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="14">
            <el-form-item label="问题类别" prop="type">
              <el-select v-model="answer_form.type" placeholder="请选择问题类别" value-key="typeId" @change="handleAnswerTypeChange" filterable :disabled="true">
                <el-option v-for="item in typeOptions" :key="item.typeId" :label="item.type" :value="item"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="问题标题" prop="title">
          <el-input v-model="answer_form.title" :disabled="true" placeholder="请输入问题标题"/>
        </el-form-item>

        <el-form-item label="严重程度" prop="severity">
          <el-radio-group v-model="answer_form.severity" :disabled="true">
            <el-radio label="轻微">轻微</el-radio>
            <el-radio label="一般">一般</el-radio>
            <el-radio label="紧急">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题内容" prop="questionContent">
          <el-input v-model="answer_form.questionContent" type="textarea" :rows="4" :disabled="true" placeholder="请输入问题内容" />
        </el-form-item>
        <el-form-item label="回答内容" prop="answerContent">
          <el-input v-model="answer_form.answerContent" type="textarea" :rows="4" placeholder="请输入回答内容" />
        </el-form-item>
        <el-form-item label="是否可解决" label-width="100px" prop="canSolve">
          <el-radio-group v-model="answer_form.canSolve">
            <el-radio label="可解决">可解决</el-radio>
            <el-radio label="无法解决">无法解决</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 回答附件上传  -->
        <el-form-item label="回答附件" prop="answerAttachments">
            <el-upload
              ref="answerUpload"
              :file-list="answer_form.answerAttachList"
              :action= "uploadFileUrl"
              :multiple="true"
              :on-preview="handleFormPreview"
              :headers="headers"
              :before-upload="handlebeforeUpload"
              :on-error="handleUploadError"
              :on-remove="handleAnswerRemove"
              :on-success="handleUploadAnswerSuccess"
              :limit="3"
              :on-exceed="handleExceed"
              list-type="picture">
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">支持上传jpg/png/pdf/xls/xlsx/doc/docx文件，且不超过10MB</div>
            </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAnswerForm">确 定</el-button>
        <el-button @click="cancelAnswer">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAnswerQa, listAnswerNotify, ansQa} from "@/api/qaSystem/qaAnswer";
import {refreshCache, openNotify, closeNotify, setNotify, markNotified, listSecurityOption, listTypeOption, getQa, listQaCommon, updateVisits, getUser, updateRemoveRes, convertToPdf, getRepliesByRecordId, addReply, delReply, thumbsUpReply, getReplyFloor, getReplyPage} from "@/api/qaSystem/qaCommon";
import { getToken } from "@/utils/auth";
import ElImageViewer from "element-ui/packages/image/src/image-viewer";

export default {
  name: "QaAnswer",
  components: {ElImageViewer},
  data() {
    return {
      userName: null,
      loginName: null,
      userId: null,
      // 是否开启消息提醒
      openNotify: true,
      // 遮罩层
      loading: true,
      // 右侧表格遮罩层
      admin_loading: true,
      replyLoading: false,
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 选中admin数组
      admin_ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // admin单个和多个禁用
      admin_single: true,
      admin_multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 左侧表格数据
      qaList: [],
      // 提醒列表
      notifyList: [],
      // 回答弹出层标题
      answer_title: "",
      // 是否显示回答表单
      answer_open: false,
      // 默认排序字段和顺序
      orderByColumn: 'viewCount',
      isAsc: 'desc',
      // 筛选自己或全部（默认查看全部）
      filterSelf: false,
      // 上传参数
      uploadFileUrl: process.env.VUE_APP_BASE_API + "/system/oss/upload", // 上传文件服务器地址
      headers: {
            Authorization: "Bearer " + getToken(),
      },
      limit: 3,
      // 图片预览(根据是否是表单选择是否打开关闭当前表单)
      isForm: true,
      showImageViewer: false,
      previewImageUrl: '',
      mode: '',
      // 文档预览
      isConverting: false,  // 转换状态标志
      convertProgress: 0,   // 转换进度（可选）
      convertMessage: '',    // 转换状态信息
      // 论坛回复
      replies: [], // 回复列表
      replyInputContent: '', // 输入框内容
      replyingTo: null, // 正在回复的楼层
      actualContent: '', // 实际发送的内容
      queryReply: {
        pageNum: 1,
        pageSize: 10,
        searchValue:""
      },
      replyTotal: 0,
      floorCache: {}, // 用于缓存楼层号
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        searchValue:""
      },
      // 右侧表格独立数据
      searchQaList: [],
      searchTotal: 0,
      searchQueryParams: {
        pageNum: 1,
        pageSize: 18,
        searchValue: "",
        searchUser: "",
        pageType: "",
        orderByColumn: "",
        isAsc: "",
      },
      // 下拉密级数据源
      securityOptions: [],
      // 下拉问题类型数据源
      typeOptions: [],
      // 回答表单参数
      answer_form: {
        createBy: undefined,
        createId: undefined,
        telephone: undefined,
        roomNumber: undefined,
        title: undefined,
        classification: undefined,
        type: undefined,
        severity: undefined,
        questionContent: undefined,
        answerContent: undefined,
        canSolve: "可解决",
        answerAttachList: []
      },
      // 回答表单校验
      answerRules: {
        canSolve: [
          { required: true, message: "参数名称不能为空", trigger: "blur" }
        ],
        answerContent: [
          { required: true, message: "参数键名不能为空", trigger: "blur" }
        ]
      },
      // 存储当前选中的问题
      selectedQuestion: {},
    };
  },
  created() {
    getUser().then(response => {
        if(response.code === 200){
          this.userName = response.data.createBy;
          // 工号
          this.loginName = response.data.createId;
          // 用户唯一ID
          this.userId = response.data.userId;
          // 获取提醒消息状态
          this.openNotify = response.data.openNotify === 1;
        }else{
          this.$message.error(`无此用户，请联系管理员`);
        }
      }).catch(error => {
        console.error(`无此用户，请联系管理员`, error);
      });
    this.getList();
    this.getSearchList();
    if(this.openNotify){
      this.getNotifyList();
    }
  },

  watch: {
    notifyList: {
      immediate: true,
      handler(list){
        if(this.openNotify && list && list.length){
          this.handleNotify(list);
        }
      }
    }
  },

  methods: {
    /** 提问者按钮：增加、修改(部分)、删除（部分）、采纳、刷新------------------------------提问者功能：增删改查采纳刷新*/
    /** 回答者按钮：回答、刷新-----------------------------------------------------------回答者功能：改查刷新*/
    /** 管理者按钮：回答、修改（全部）、删除（全部）、采纳、刷新、导出-----------------回答者功能：增删改查采纳导出刷新*/

    /**                      三角色通用方法（查、刷新）                   */
    /** 获取下拉问题类型栏数据 */
    async getTypeOptions(){
      await listTypeOption().then(response => {
          this.typeOptions = response.data || [];
          
          // 排序显示在表单中
          this.typeOptions.sort((a, b) => a.sequence - b.sequence);
        }).catch(error =>{
          console.error("获取问题类型失败", error);
        });
    },
    // 下拉问题类型选择变化
    handleAnswerTypeChange(val){
      this.answer_form.type = val.type;
    },
    /** 获取下拉密级栏数据 */
    async getSecurityOptions(){
      // 只获取与数据相关的密级
      const securityType = "data";
      await listSecurityOption(securityType).then(response => {
          this.securityOptions = response.data || [];
          // 排序显示在表单中
          this.securityOptions.sort((a, b) => a.sort - b.sort);
        }).catch(error =>{
          console.error("获取密级信息失败", error);
        });
    },
    // 下拉密级选择变化
    handleAnswerSecurityChange(val){
      this.answer_form.classification = val.securityName;
    },
    /** 左侧查询参数列表：提问者、回答者、管理者后端方法需要区别（TODO） */
    getList() {
      this.loading = true;
      listAnswerQa(this.queryParams).then(response => {
          this.qaList = response.rows;
          this.total = response.total;
          this.loading = false;
        }).catch(error =>{
          console.error("获取问题列表失败", error);
          this.loading = false;
        });
    },
    // 处理排序命令
    handleSortCommand(command) {
      if (this.orderByColumn === command) {
        // 如果点击的是当前排序字段，则切换排序顺序
        this.isAsc = this.isAsc === 'desc' ? 'asc' : 'desc';
      } else {
        // 否则设置新的排序字段，默认降序
        this.orderByColumn = command;
        this.isAsc = 'desc';
      }
      // 重新获取数据
      this.getSearchList();
    },

    /** 开启消息开关 */
    openMessage(){
      openNotify().then(response => {
        this.openNotify = true;
        this.$message.success(response.msg);
      }).catch(error => {
        console.error("消息开启失败", error);
      })
    },

    /** 关闭消息开关 */
    closeMessage(){
      closeNotify().then(response => {
        this.openNotify = false;
        this.$message.success(response.msg);
      }).catch(error => {
        console.error("消息关闭失败", error);
      })
    },

    /** 查询需提醒的问题回答列表 */
    getNotifyList(){
      listAnswerNotify().then(response => {
        this.notifyList = response.data;
      }).catch(error => {
        console.error("获取提醒数据失败", error);
      })
    },

    /** 提醒流程 */
    handleNotify(list){
      if(!list || !list.length) return
      // 1.拼接所有的note
      const content = `您有${list.length}条问题待回答，请尽快处理！`;
      // 2.收集ids
      const ids = list.map(qa => qa.recordId);
      // 3.右下角弹框
      this.$notify({
        title: '提醒',
        position: 'bottom-right',
        duration: 5000,
        dangerouslyUseHTMLString: false,
        offset: 24,
        showClose: true,
        onClose: () =>{
          // 4. 后端置0
          markNotified(ids).then(response =>{
            // 5. 本地置0
            list.forEach(qa => this.$set(qa, 'notify', 0))
          }).catch(error => {
            console.error("提醒消息服务出现故障", error);
          })
        },
        message: content,
      }); 
    },


    /** 问题总表查询方法 */
    getSearchList() {
      this.admin_loading = true;
      // 添加排序参数
      this.searchQueryParams.orderByColumn = this.orderByColumn;
      this.searchQueryParams.isAsc = this.isAsc;
      this.searchQueryParams.pageType = "answer";
      listQaCommon(this.searchQueryParams).then(response => {
        this.searchQaList = response.rows;
        this.searchTotal = response.total;
        this.admin_loading = false;
      }).catch(error => {
        console.error("获取问题总表列表失败", error);
        this.admin_loading = false;
      });
    },
    // 处理查看人命令
    handleReadCommand(command) {
      this.filterSelf = command === "self";
      if (this.filterSelf){
        this.searchQueryParams.searchUser = "self";
      }else{
        this.searchQueryParams.searchUser = "";
      }
      // 重新获取数据
      this.getSearchList();
    },
    /** 搜索按钮操作 只影响问题总表*/
    handleQuery() {
      this.searchQueryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 只影响问题总表*/
    resetQuery() {
      this.searchQueryParams = {
        pageNum: 1,
        pageSize: 10,
        searchValue: ""
      };
      this.searchQueryParams.pageNum = 1;
      this.getSearchList();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    // 右侧表格多选框选中数据
    handleAdminSelectionChange(selection) {
      this.admin_ids = selection.map(item => item.recordId)
      this.admin_single = selection.length!=1
      this.admin_multiple = !selection.length
    },
    /** 刷新缓存按钮操作 */
    handleRefreshCache() {
      refreshCache().then(() => {
        /** 清除浏览器数据 */
        localStorage.clear();
        sessionStorage.clear();
        /** 强制刷新页面 */
        location.reload(true);
        this.$message.success("刷新成功");
      });
    },
    // 处理行点击事件
    handleClick(row) {
      this.selectedQuestion = row;
      getQa(row.recordId, "admin").then(response => {
        this.selectedQuestion.questionAttachList = response.data.questionAttachList || [];
        this.selectedQuestion.answerAttachList = response.data.answerAttachList || [];
      });
      this.handleUpdateVisits(row.recordId)  // 增加浏览量
      this.loadReplies(row.recordId); // 加载回复
    },

    // 提醒问题提出人员采纳问题
    handleRemind(row){
      setNotify(row.recordId).then(response => {
        this.$message.success(response.msg)
      }).catch(error => {
        console.error("获取提醒数据失败", error);
      })
    },

    // 更新浏览量
    handleUpdateVisits(recordId) {
      updateVisits(recordId).then(response => {
        if (response.code === 200) {
          // this.getSearchList();
        }
        }).catch(error => {
          this.$message.error('更新访问量失败', response.msg);
        });
    },

    // 处理附件逻辑
    async handleFormPreview(file) {
      const url = file.url;
      const extension = url.split('.').pop().toLowerCase();
      // 图片预览
      if (['png', 'jpg', 'jpeg'].includes(extension)) {
        this.previewImageUrl = url;
        this.showImageViewer = true;
        this.closeAllPanels();
      }
      // PDF直接在新窗口打开
      else if (extension === 'pdf') {
        window.open(url, '_blank');
      }
      else if (['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(extension)) {
        this.isConverting = true;
        this.convertMessage = `${extension.toUpperCase()}格式文件正在转换为PDF预览模式，请稍候...`;

        try {
          const response = await fetch(file.url);
          const arrayBuffer = await response.arrayBuffer();

          // 模拟进度更新
          const progressInterval = setInterval(() => {
            this.convertProgress = Math.min(this.convertProgress + 10, 90);
            // 动态更新进度提示
            this.convertMessage = `${extension.toUpperCase()}格式文件预览模式 ${this.convertProgress}%...`;
          }, 300);

          const pdfUrl = await this.convertOfficeToPdf(arrayBuffer, file.name);

          clearInterval(progressInterval);
          this.convertProgress = 100;
          this.convertMessage = `${extension.toUpperCase()}转换完成，正在打开预览...`;

          await new Promise(resolve => setTimeout(resolve, 500));
          window.open(pdfUrl, '_blank');

        } catch (error) {
          this.$message.error(`${extension.toUpperCase()}文件预览失败: ${error.message}`);
          window.open(file.url, '_blank');
        } finally {
          this.isConverting = false;
          this.convertProgress = 0;
        }
      }
      else{
        // 下载文档
        window.location.href = file.url;
      }
    },

    // 处理附件逻辑
    async handlePreview(file) {
      const url = file.url;
      const extension = url.split('.').pop().toLowerCase();
      // 图片预览
      if (['png', 'jpg', 'jpeg'].includes(extension)) {
        this.previewImageUrl = url;
        this.showImageViewer = true;
        // 不是表单则在关闭预览后不需要再打开表单
        this.isForm = false;
      }
      // PDF直接在新窗口打开
      else if (extension === 'pdf') {
        window.open(url, '_blank');
      }
      else if (['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(extension)) {
        this.isConverting = true;
        this.convertMessage = `${extension.toUpperCase()}格式文件正在转换为PDF预览模式，请稍候...`;

        try {
          const response = await fetch(file.url);
          const arrayBuffer = await response.arrayBuffer();

          // 模拟进度更新
          const progressInterval = setInterval(() => {
            this.convertProgress = Math.min(this.convertProgress + 10, 90);
            // 动态更新进度提示
            this.convertMessage = `${extension.toUpperCase()}格式文件预览模式 ${this.convertProgress}%...`;
          }, 300);

          const pdfUrl = await this.convertOfficeToPdf(arrayBuffer, file.name);

          clearInterval(progressInterval);
          this.convertProgress = 100;
          this.convertMessage = `${extension.toUpperCase()}转换完成，正在打开预览...`;

          await new Promise(resolve => setTimeout(resolve, 500));
          window.open(pdfUrl, '_blank');

        } catch (error) {
          this.$message.error(`${extension.toUpperCase()}文件预览失败: ${error.message}`);
          window.open(file.url, '_blank');
        } finally {
          this.isConverting = false;
          this.convertProgress = 0;
        }
      }
      else{
        // 下载文档
        window.location.href = file.url;
      }
    },
    // 在组件中使用
    async convertOfficeToPdf(fileData, fileName) {
      try {
        const response = await convertToPdf(fileData, fileName);
        console.log('文档转换成功', response);
        if (response.code === 200) {
          return response.data.fileUrl; // 假设返回数据结构为 { code: 200, data: { pdfUrl: '...' } }
        } else {
          throw new Error(response.msg || '文档转换失败');
        }
      } catch (error) {
        console.error('文档转换失败', error);
        throw error;
      }
    },
    // 关闭所有可能的面板
    closeAllPanels() {
      if (this.mode === "question") this.question_open = false;
      if (this.mode === "answer") this.answer_open = false;
      if (this.mode === "admin") this.admin_open = false;
    },
    // 删除左下角展示区域的附件
    handleRemoveAttachment(file, type) {
      this.$confirm('确认不展示该附件吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('清除展示附件成功');
        // 根据类型更新对应的附件列表
        if (type === 'question') {
          this.selectedQuestion.questionAttachList = this.selectedQuestion.questionAttachList.filter(item => item.ossId !== file.ossId);
        }else{
          this.selectedQuestion.answerAttachList = this.selectedQuestion.answerAttachList.filter(item => item.ossId !== file.ossId);
        }
      }).catch(() => {
        // 用户取消删除
      });
    },
    truncateFilename(filename, textlen) {
      if (!filename) return '';

      // 如果是中文文件名，按字符数截断
      if (/[\u4e00-\u9fa5]/.test(filename)) {
        if (filename.length > textlen) {
          const nameWithoutExt = filename.lastIndexOf('.') > 0
            ? filename.substring(0, filename.lastIndexOf('.'))
            : filename;
          const ext = filename.lastIndexOf('.') > 0
            ? filename.substring(filename.lastIndexOf('.'))
            : '';
          return nameWithoutExt.substring(0, textlen) + '...' + ext;
        }
        return filename;
      }

      // 非中文文件名，按CSS的text-overflow处理
      return filename;
    },
    // 关闭图片弹窗
    closeImageViewer(){
      this.showImageViewer = false;
      this.previewImageUrl = '';
      if(this.mode === "question" && this.isForm){this.question_open = true;}
      if(this.mode === "answer" && this.isForm){this.answer_open = true;}
      if(this.mode === "admin" && this.isForm){this.admin_open = true;}
      // 不论是否打开表单，最后都将isForm恢复
      this.isForm = true;
    },
    // 上传失败
    handleUploadError(err) {
      this.$message.error("上传失败, 请重试");
    },

    // 上传前验证
    handlebeforeUpload(file) {
      const isLt10M = file.size / 1024 / 1024 < 10
      const isAllowedType = ['image/jpeg', 'image/png', 'application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                            'application/msword', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      ]
                            .includes(file.type)
      if (!isAllowedType) {
        this.$message.error('只支持上传jpg/png/pdf/doc/docx/xls/xlsx文件!')
        return false
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过10MB!')
        return false
      }
      return true
    },
    // 处理上传超出限制
    handleExceed() {
      this.$message.error(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    // 加载回复
    async loadReplies(recordId) {
      if (!recordId) {
        this.replies = [];
        this.replyTotal = 0;
        return;
      }
      this.replyLoading = true;
      this.queryReply.searchValue = recordId;
      try {
        const response = await getRepliesByRecordId(this.queryReply);
        if (response.code === 200) {
          // 计算楼层号
          const startFloor = (this.queryReply.pageNum - 1) * this.queryReply.pageSize;
          this.replies = (response.rows || []).map((reply, index) => ({
            ...reply,
            floorNumber: startFloor + index + 1
          }));
          this.replyTotal = response.total || 0;
          this.replies.forEach((reply) => {
            this.$set(this.floorCache, reply.replyId, reply.floorNumber);
          });
          if (this.queryReply.pageNum === 1 && this.$refs.replyListScroll) {
            this.$nextTick(() => {
              this.$refs.replyListScroll.scrollTop = this.$refs.replyListScroll.scrollHeight;
            });
          }
        }
      } finally {
        this.replyLoading = false;
      }
    },
    // 发送回复
    async handleSendReply() {
      // 提取实际内容（去掉"回复XXX:"部分）
      let actualContent = this.replyInputContent;
      if (this.replyingTo && this.replyInputContent.startsWith(`回复${this.replyingTo.createBy}:`)) {
        actualContent = this.replyInputContent.replace(`回复${this.replyingTo.createBy}:`, '').trim();
      }

      if (!actualContent) {
        this.$message.warning('回复内容不能为空');
        return;
      }

      // 创建临时回复项
      const tempReply = {
        replyId: 'temp-' + Date.now(),
        recordId: this.selectedQuestion.recordId,
        content: actualContent,
        createBy: this.$store.state.user.name,
        createId: this.$store.state.user.userId,
        createTime: new Date().toISOString(),
        thumbsUp: 0,
        replyToId: this.replyingTo ? this.replyingTo.replyId : null,
        replyToUser: this.replyingTo ? this.replyingTo.createBy : null,
        isTemp: true
      };

      // 添加到回复列表前检查是否需要跳转新页
      const shouldGoToNewPage = this.replies.length >= this.queryReply.pageSize;

      if (shouldGoToNewPage) {
        this.queryReply.pageNum = Math.ceil((this.replyTotal + 1) / this.queryReply.pageSize);
        await this.loadReplies(this.selectedQuestion.recordId);
        this.replies.push(tempReply);
      } else {
        this.replies.push(tempReply);
      }
      this.replyInputContent = '';
      this.replyingTo = null;

      // 滚动到底部
      this.scrollToBottom();

      try {
        // 发送到后端
        const response = await addReply({
          recordId: this.selectedQuestion.recordId,
          content: actualContent,
          replyToId: tempReply.replyToId
        });

        if (response.code === 200) {
          this.$message.success('发送成功');
          if (shouldGoToNewPage) {
            // 如果是新页，数据已从服务端加载，无需替换
            this.replies = this.replies.map(r =>
              r.replyId === tempReply.replyId ? { ...response.data, isTemp: false } : r
            );
          }else{
            // 替换临时回复
            const index = this.replies.findIndex(r => r.replyId === tempReply.replyId);
            if (index !== -1) {
              this.replies.splice(index, 1, {...response.data, isTemp: false
              });
            }
          }
          // 再次确保滚动到底部
          this.scrollToBottom();
        } else {
          this.$message.error(response.msg || '发送失败');
          this.replies = this.replies.filter(r => r.replyId !== tempReply.replyId);
        }
      } catch (error) {
        console.error('发送失败', error);
        this.$message.error('发送失败，请重试');
        this.replies = this.replies.filter(r => r.replyId !== tempReply.replyId);
      }
    },
    // 回复指定楼层
    handleReplyTo(reply) {
      this.replyingTo = {
        replyId: reply.replyId,
        createBy: reply.createBy,
        floorNumber: this.getReplyFloorNumber(reply.replyId)
      };

      // 设置输入框显示内容
      this.replyInputContent = `回复${this.replyingTo.createBy}: `;
      // 实际发送内容初始为空
      this.actualContent = '';

      this.$nextTick(() => {
        const input = document.querySelector('.reply-input textarea') ||
                    document.querySelector('.reply-input .el-input__inner');
        if (input) {
          input.focus();
          input.setSelectionRange(this.replyInputContent.length, this.replyInputContent.length);
        }
        // 回复指定楼层时也滚动到底部
        this.scrollToBottom();
      });
    },

    // 删除回复处理函数
    handleReplyDel(reply) {
      this.$confirm('确定要删除这条回复吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 用户点击确定后执行删除
        delReply(reply.replyId).then(response => {
          if (response.code === 200) {
            this.$message.success('删除成功');
          }
          // 删除成功后刷新回复列表
          this.loadReplies(this.selectedQuestion.recordId);
        }).catch(error => {
          this.$message.error(response.msg);
        });
      }).catch(() => {
        // 用户点击取消
        this.$message.info('用户取消删除');
      });
    },

    // 获取回复的楼层号
    getReplyFloorNumber(replyId) {
      // 先尝试在当前页查找
      const localReply = this.replies.find(r => r.replyId === replyId);
      if (localReply) {
        const index = this.replies.indexOf(localReply);
        return (this.queryReply.pageNum - 1) * this.queryReply.pageSize + index + 1;
      }
      getReplyFloor(replyId).then(response => {
        if (response.code === 200) {
          this.$set(this.floorCache, replyId, response.data);
          return response.data;
        }
      }).catch(error => {
        this.$message.error('获取楼层号失败', error);
      })
      return '引用楼层已删除';
    },

    // 滚动到讨论区底部
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.replyListScroll;
        if (container) {
          // 使用平滑滚动
          container.scrollTo({
            top: container.scrollHeight,
            behavior: 'smooth'
          });
        }
      });
    },

    // 滚动到指定回复
    scrollToReply(replyId) {
      // 先检查是否在当前页
      const localReply = this.replies.find(r => r.replyId === replyId);
      if (localReply) {
        this.scrollToElement(replyId);
        return;
      }
        // 保存当前滚动位置
      const scrollContainer = this.$refs.replyListScroll;
      const oldScrollTop = scrollContainer?.scrollTop || 0;
      getReplyPage({
        replyId,
        pageSize: this.queryReply.pageSize
      }).then(response => {
        if (response.code === 200){
          // 切换到目标页
          this.queryReply.pageNum = response.data;
          this.loadReplies(this.selectedQuestion.recordId);
          // 等待数据加载完成后滚动到目标元素
          this.$nextTick(() => {
            this.scrollToReply(replyId);
            // this.scrollToElement(replyId);
          });
          // this.scrollToReply(replyId);
        }
      }).catch(error => {
        console.error('获取回复页码失败', error);
        this.$message.error('无法定位到该回复');
      })
    },

    // 辅助方法：滚动到指定元素
    scrollToElement(replyId) {
      const container = this.$refs.replyListScroll;
      const element = document.getElementById(`reply-${replyId}`);

      if (container && element) {
        // 计算元素相对于容器的位置
        const elementRect = element.getBoundingClientRect();
        const containerRect = container.getBoundingClientRect();
        const scrollTop = elementRect.top - containerRect.top + container.scrollTop - 20; // 减去20px留出空间

        // 平滑滚动
        this.smoothScrollTo(container, scrollTop, 500);

        // 高亮效果
        element.classList.add('highlight-reply');
        setTimeout(() => {
          element.classList.remove('highlight-reply');
        }, 2000);
      }
    },

    // 平滑滚动辅助函数
    smoothScrollTo(element, to, duration) {
      const start = element.scrollTop;
      const change = to - start;
      const startTime = performance.now();

      const animateScroll = (currentTime) => {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        element.scrollTop = start + change * progress;

        if (progress < 1) {
          window.requestAnimationFrame(animateScroll);
        }
      };

      window.requestAnimationFrame(animateScroll);
    },
    // 滚动到顶部方法
    scrollToTop() {
      this.$nextTick(() => {
        const container = this.$refs.replyListScroll;
        if (container) {
          container.scrollTo({ top: 0, behavior: 'smooth' });
        }
      });
    },
    // 点赞
    handleThumbsUp(reply, timesout) {
      // 判断是否冷却
      if (reply.replyLikeCooldown){
        this.$message.warning("点赞频繁, 请3秒后再试");
        return;
      }
      // 图片置灰
      this.$set(reply, "replyLikeCooldown", true);
      thumbsUpReply(reply.replyId).then(response => {
        if (response.code === 200) {
          this.$set(reply, "thumbsUp", (reply.thumbsUp || 0) + 1)
          // reply.thumbsUp = (reply.thumbsUp || 0) + 1;
          this.$message.success('点赞成功');
        } else {
          this.$message.error(response.msg || '点赞失败');
        }
      }).catch(error => {
        console.error('点赞失败', error);
      }).finally(() =>{
      });
      // 3秒后解除冷却
      setTimeout(() =>{
        this.$set(reply, "replyLikeCooldown", false);
      }, timesout);
    },

    /**                      提问者、管理者方法（增、删、改、采纳）                   */
    /** 表格分页功能汇总 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.queryParams.pageNum = 1;
      this.getList();
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val;
      this.getList();
    },
    handleSearchSizeChange(val) {
      this.searchQueryParams.pageSize = val;
      this.searchQueryParams.pageNum = 1;
      this.getSearchList();
    },

    handleSearchCurrentChange(val) {
      this.searchQueryParams.pageNum = val;
      this.getSearchList();
    },
    async handleReplySizeChange(val) {
      this.queryReply.pageSize = val;
      this.queryReply.pageNum = 1;
      await this.loadReplies(this.selectedQuestion.recordId);
    },

    async handleReplyCurrentChange(val) {
      this.queryReply.pageNum = val;
      this.loadReplies(this.selectedQuestion.recordId);
      this.scrollToTop(); // 翻页后滚动到顶部
    },

    /**                      回答者、管理者方法                   */
    /** 回答按钮操作（问题状态为待处理、已处理且是否可解决状态为不予处理时才可回答，采纳状态和管理员处理状态不可回答） */
    handleAnswer(row) {
      this.resetAnswerForm();
      const recordId = row.recordId || this.ids;
      this.mode = "answer";
      getQa(recordId, this.mode).then(response => {
        this.answer_form = {
          recordId: response.data.recordId,
          createBy: response.data.createBy,
          createId: response.data.createId,
          telephone: response.data.telephone,
          roomNumber: response.data.roomNumber,
          title: response.data.title,
          classification: response.data.classification,
          type: response.data.type,
          severity: response.data.severity,
          questionContent: response.data.questionContent,
          answerContent: response.data.answerContent,
          canSolve: response.data.canSolve,
          // TODO 将（修改的后端参数加入answerAttachments的参数）
          answerAttachList: response.data.answerAttachList || []
        };
        this.answer_open = true;
        this.answer_title = "回答问题";
      });
    },
    /** 回答表单提交按钮 */
    submitAnswerForm() {
      this.$refs["answer_form"].validate(valid => {
        if (valid) {
          ansQa(this.answer_form).then(response => {
            if(response.code === 200){
              this.$message.success('回答成功');
              this.answer_open = false;
              this.getList();
              this.getSearchList(); // 刷新搜索表格
            }else{
              this.$message.error("回答失败" || response.msg);
            }
          }).catch(error => {
            console.error('回答失败', error);
          });
        }
      });
    },
    // 回答表单取消按钮
    cancelAnswer() {
      this.answer_open = false;
      this.resetAnswerForm();
    },
    // 回答表单重置
    resetAnswerForm() {
      this.answer_form = {
        createBy: undefined,
        createId: undefined,
        telephone: undefined,
        roomNumber: undefined,
        title: undefined,
        classification: undefined,
        type: undefined,
        severity: undefined,
        questionContent: undefined,
        answerContent: undefined,
        canSolve: "可解决",
        answerAttachList: []
      };
      this.resetForm("answer_form");
    },
    // 上传回答附件成功回调
    handleUploadAnswerSuccess(res, file, fileList) {
      if(res.code === 200){
        // 检查是否已存在相同ossId的附件
        const exists = this.answer_form.answerAttachList.some(
          item => item.ossId === res.data.ossId
        );
        if(!exists) {
          const newAttachment = {
            name: file.name,
            url: res.data.url,
            ossId: res.data.ossId,
            fileSize: file.size,
            isDeleted: 0,
            attachType: "answer"
          };
          this.answer_form.answerAttachList.push(newAttachment);
          // 手动更新el-upload的文件列表
          this.$nextTick(() => {
            if(this.$refs.answerUpload) {
              this.$refs.answerUpload.uploadFiles = fileList.map(f => ({
                ...f,
                status: f.status === 'success' ? 'success' : f.status,
                percentage: f.percentage || 100
              }));
            }
          });
        }
        this.$message.success(`${file.name} 上传成功`);
      }else{
        this.$message.error(`${file.name} 上传失败: ${res.msg || '未知错误'}`);
      }
    },
    // 删除问题附件成功回调（删除sys_oss数据库中的内容）
    handleAnswerRemove(file, fileList) {
      // 从表单的questionAttachList数组中移除对应的附件
      let ossId = null;
      if(fileList.length === 0){
        this.answer_form.answerAttachList.pop();
        ossId = file.ossId;
      }else{
        ossId = file.response.data.ossId;
        this.answer_form.answerAttachList = this.answer_form.answerAttachList.filter(
        item => item.ossId !== ossId
        );
      }
      if(!ossId){
        this.$message.error("无法获取附件OSSID");
      }
      updateRemoveRes(ossId).then((response) =>{
        if(response.code === 200){
          this.$message.success("删除成功");
        }
      }).catch(err =>{
        this.$message.error("删除失败" + err);
      })
    },
  }
};
</script>

<style scoped>
/* 自定义表格边框样式 */
.custom-bordered-table {
  border: 1px solid #EBEEF5;
  border-right: 1px solid #EBEEF5;
}
.custom-bordered-table .el-table__header-wrapper,
.custom-bordered-table .el-table__body-wrapper {
  border-left: 1px solid #EBEEF5;
  border-right: 1px solid #EBEEF5;
}
.custom-bordered-table th,
.custom-bordered-table td {
  border-right: 1px solid #EBEEF5;
}
.custom-bordered-table th:first-child,
.custom-bordered-table td:first-child {
  border-left: 1px solid #EBEEF5;
}
.empty-placeholder {
  border: 1px solid #EBEEF5;
  background-color: #f5f7fa;
}
.pagination-wrapper{
  /* display: flex;
  justify-content: flex-end; */
  text-align: right;
  margin-top: 3px;
  margin-bottom: 3px;
}
.el-table--border{
  border-color: #EBEEF5;
}
.paper-yellow {
  background-color: #ffffe0; /* 浅黄色 */
  /* 其他可选浅黄色值：
     #fffacd - 柠檬绸色(更亮的浅黄)
     #fff8dc - 玉米丝色(偏白的浅黄)
     #ffffcc - 更亮的浅黄
     #fffdd0 - 奶油色
  */
}
/* 统一行间距 */
.info-row {
  margin-bottom: 5px;
}
/* 转换弹窗样式 */
.converting-dialog {
  .el-dialog__body {
    padding: 30px 20px;
  }
  p {
    margin: 0;
    font-size: 16px;
    color: #606266;
  }
}
.highlight-reply {
  background-color: #f5f5dc !important;
  transition: background-color 0.5s ease;
  animation: pulse 2s;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(245, 245, 220, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(245, 245, 220, 0); }
  100% { box-shadow: 0 0 0 0 rgba(245, 245, 220, 0); }
}
.status-待处理 { color: #ff4d4f; }
.status-已处理 { color: #faad14; }
.status-已采纳 { color: #52c41a; }

/* 通知条宽度*/
.el-notification {
  width: 340px !important;
}
</style>
