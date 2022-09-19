import { useEffect } from 'react';
import { Outlet } from 'react-router-dom';

import Landing from '@/pages/host/Landing';

import useModal from '@/hooks/useModal';

const DefaultLayout: React.FC = () => {
  const isRootPath = location.pathname === '/';

  const { closeModal } = useModal();

  useEffect(() => {
    closeModal();
    scrollTo(0, 0);
  }, []);

  return <>{isRootPath ? <Landing /> : <Outlet />}</>;
};

export default DefaultLayout;
