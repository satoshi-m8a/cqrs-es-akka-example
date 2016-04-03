import {Discussion} from '../model/discussion';
import {Comment} from '../model/comment';

export interface IDiscussionAction {
    type:string;
    discussion?:Discussion;
    discussions?:Array<Discussion>;
    comment?:Comment;
    comments?:Array<Comment>;
}

export function loadDiscussion(discussion:Discussion):IDiscussionAction {
    return {
        type: 'LOAD_DISCUSSION',
        discussion: discussion
    };
}

export function loadDiscussions(discussions:Array<Discussion>):IDiscussionAction {
    return {
        type: 'LOAD_DISCUSSIONS',
        discussions: discussions
    };
}

export function loadComments(comments:Array<Comment>):IDiscussionAction {
    return {
        type: 'LOAD_COMMENTS',
        comments: comments
    };
}

export function addComment(comment:Comment):IDiscussionAction {
    return {
        type: 'ADD_COMMENT',
        comment: comment
    };
}
