import {DiscussionsStore} from '../../state/discussions.store';
import {Component} from 'angular2/core';
import {CommentListComponent} from '../../component/comment-list/comment-list.component';
import {RouteParams} from 'angular2/router';

@Component({
    templateUrl: 'app/discussions/view/discussion-detail/discussion-detail.view.html',
    directives: [CommentListComponent]
})
export class DiscussionDetailView {
    constructor(private discussionsStore:DiscussionsStore, params:RouteParams) {
        let id = params.get('id');
        discussionsStore.getDiscussion(id);
        discussionsStore.getComments(id);
    }
}
