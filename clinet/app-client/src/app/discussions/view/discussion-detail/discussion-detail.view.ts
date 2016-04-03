import {DiscussionsStore} from '../../state/discussions.store';
import {Component} from 'angular2/core';
import {CommentListComponent} from '../../component/comment-list/comment-list.component';
import {RouteParams} from 'angular2/router';
import {AddCommentModalComponent} from '../../component/add-comment-modal/add-comment-modal.component';

declare var jQuery:any;

@Component({
    templateUrl: 'app/discussions/view/discussion-detail/discussion-detail.view.html',
    directives: [CommentListComponent, AddCommentModalComponent]
})
export class DiscussionDetailView {
    constructor(private discussionsStore:DiscussionsStore, params:RouteParams) {
        let id = params.get('id');
        discussionsStore.getDiscussion(id);
        discussionsStore.getComments(id);
    }

    onClickAddComment() {
        jQuery('#' + this.discussionsStore.discussion.id).modal('show');
    }
}
