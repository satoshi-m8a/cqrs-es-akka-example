import {Discussion} from '../model/discussion';
export interface IDiscussionAction {
    type:string;
    discussion?:Discussion;
    discussions?:Array<Discussion>;
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
