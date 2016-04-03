import {IDiscussionAction} from './discussions.action';
import {Discussion} from '../model/discussion';
import {Comment} from '../model/comment';

export function reducerDiscussion(state:Discussion = new Discussion('', ''), action:IDiscussionAction) {
    switch (action.type) {
        case 'LOAD_DISCUSSION':
            return action.discussion;
        default:
            return state;
    }
}

export function reducerDiscussions(state:Array<Discussion> = [], action:IDiscussionAction) {
    switch (action.type) {
        case 'LOAD_DISCUSSIONS':
            return action.discussions;
        default:
            return state;
    }
}

export function reducerComments(state:Array<Comment> = [], action:IDiscussionAction) {
    switch (action.type) {
        case 'LOAD_COMMENTS':
            return action.comments;
        case 'ADD_COMMENT':
            state.push(action.comment);
            return state;
        default:
            return state;
    }
}
