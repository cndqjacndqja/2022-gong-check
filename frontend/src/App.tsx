import routes from './Routes';
import ToastBar from './components/common/ToastBar';
import Transitions from '@/Transitions';
import { useRoutes } from 'react-router-dom';
import { useRecoilValue } from 'recoil';

import useTransitionSelect from '@/hooks/useTransitionSelect';

import { isShowModalState, modalComponentState } from '@/recoil/modal';
import { isShowToastState } from '@/recoil/toast';

const App = () => {
  const content = useRoutes(routes, location);

  const isShowModal = useRecoilValue(isShowModalState);
  const modalComponent = useRecoilValue(modalComponentState);

  const isShowToast = useRecoilValue(isShowToastState);

  const transition = useTransitionSelect();

  return (
    <>
      <Transitions pageKey={location.pathname} transition={transition}>
        {content}
      </Transitions>
      {isShowModal && modalComponent}
      {isShowToast && <ToastBar />}
    </>
  );
};

export default App;
